package com.lulobank.credits.services.features.payment;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.credits.sdk.dto.payment.CustomError;
import com.lulobank.credits.sdk.dto.payment.InstallmentErrorCustom;
import com.lulobank.credits.sdk.dto.payment.InstallmentPaid;
import com.lulobank.credits.sdk.dto.payment.InstallmentPaidResponse;
import com.lulobank.credits.sdk.dto.payment.PaymentInstallment;
import com.lulobank.credits.services.utils.PaymentCreditErrors;
import com.lulobank.credits.services.utils.SavingAccountState;
import com.lulobank.credits.v3.dto.LoanStatusV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetAccountRequest;
import flexibility.client.models.response.GetAccountResponse;
import flexibility.client.models.response.PaymentResponse;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.control.Option;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static com.lulobank.credits.services.utils.CreditsErrorResultEnum.CREDIT_NOT_EXIST;
import static com.lulobank.credits.services.utils.CreditsErrorResultEnum.FORBIDDEN_CLIENT;
import static com.lulobank.credits.services.utils.CreditsErrorResultEnum.SAVING_ACCOUNT_BLOCKED;
import static com.lulobank.credits.services.utils.HttpCodes.FORBIDDEN;
import static com.lulobank.credits.services.utils.HttpCodes.METHOD_NOT_ALLOWED;
import static com.lulobank.credits.services.utils.HttpCodes.NOT_FOUND;
import static com.lulobank.credits.services.utils.LogMessages.SDK_FLEXIBILITY_ERROR;
import static com.lulobank.credits.services.utils.MapperBuilder.getPaymentRequest;
import static com.lulobank.credits.services.utils.PaymentCreditErrors.ERROR_PAST_OVERDRAFT;
import static com.lulobank.credits.v3.port.in.loan.LoanState.APPROVED;
import static com.lulobank.credits.v3.port.in.loan.LoanState.CLOSED;
import static java.lang.Boolean.TRUE;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class PaymentInstallmentHandler implements Handler<Response<InstallmentPaidResponse>, PaymentInstallment> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentInstallmentHandler.class);
    //TODO: este FlexibilitySdk debe estar como una interfaz en el starter
    private final FlexibilitySdk flexibilitySdk;
    private final CreditsV3Repository creditsRepository;

    public PaymentInstallmentHandler(FlexibilitySdk flexibilitySdk, CreditsV3Repository creditsRepository) {
        this.flexibilitySdk = flexibilitySdk;
        this.creditsRepository = creditsRepository;
    }

    @Override
    public Response<InstallmentPaidResponse> handle(PaymentInstallment request) {
        return creditsRepository.findByIdCreditAndIdLoanAccountMambu(request.getIdCredit(), request.getIdCreditCBS())
                .map(creditsEntity -> validateClientId(creditsEntity.getIdClient(), request.getIdClient()) ?
                        makePayment(request, creditsEntity) :
                        generateForbiddenClientError())
                .getOrElse(() -> new Response<>(getListValidations(CREDIT_NOT_EXIST.name(), NOT_FOUND)));
    }

    private Response<InstallmentPaidResponse> generateForbiddenClientError() {
        return new Response<>(getListValidations(FORBIDDEN_CLIENT.name(),
                FORBIDDEN));
    }

    private boolean validateClientId(String entityIdClient, String requestIdClient) {
        return entityIdClient.equalsIgnoreCase(requestIdClient);
    }

    @NotNull
    private Response<InstallmentPaidResponse> makePayment(PaymentInstallment request, CreditsV3Entity creditsEntity) {
        Response<InstallmentPaidResponse> response;
        List<GetAccountResponse> accounts = null;
        try {
            accounts = flexibilitySdk.getAccountsByClientId(getAccountRequest(creditsEntity));
            response = validatePayment(request, creditsEntity, accounts);
        } catch (ProviderException e) {
            LOGGER.error(SDK_FLEXIBILITY_ERROR.getMessage(), e.getMessage(), e.getErrorCode());
            response = getResponseFromCoreBanking(creditsEntity, accounts, e);
        }
        return response;
    }

    @NotNull
    private Response<InstallmentPaidResponse> getResponseFromCoreBanking(CreditsV3Entity credit, List<GetAccountResponse> accounts, ProviderException e) {
        Response response;
        if (Boolean.TRUE.equals(validatePaymentLoanError(e))) {
            // this class have errors in compilitation time the provided argument doesnt match with constructor
            response = new Response<>
                    (getCustomErrors(accounts, credit, ERROR_PAST_OVERDRAFT.getErrorMessage(),
                            ERROR_PAST_OVERDRAFT.getErrorCode()));
        } else {
            response = new Response<>(getListValidations(e.getMessage(), String.valueOf(HttpStatus.NOT_ACCEPTABLE.value())));
        }
        return response;
    }

    @NotNull
    private Response<InstallmentPaidResponse> validatePayment(PaymentInstallment request, CreditsV3Entity credit, List<GetAccountResponse> accounts) throws ProviderException {
        Response response;
        if (isSavingAccountBlocked.test(credit, accounts)) {
            response = new Response<>(getListValidations(SAVING_ACCOUNT_BLOCKED.name(),
                    METHOD_NOT_ALLOWED));
        } else {
            //TODO: enviar campo de aplicación de cuota extra ordinaria
            PaymentResponse paymentResponse = flexibilitySdk.paymentLoan(getPaymentRequest(request, credit));
            setLoanStatusClosedAndSave(credit, request);
            creditsRepository.save(credit);
            response = new Response(getInstallmentPaid(paymentResponse, credit));
        }
        return response;
    }

    private BiPredicate<CreditsV3Entity, List<GetAccountResponse>> isSavingAccountBlocked = (creditsEntity, listAccounts) ->
            listAccounts.stream()
                    .anyMatch(account -> (account.getNumber().equals(creditsEntity.getIdSavingAccount()) && SavingAccountState.LOCKED.name().equals(account.getState())));


    private Boolean validatePaymentLoanError(ProviderException e) {
        return e.getErrorCode().equalsIgnoreCase(ERROR_PAST_OVERDRAFT.getErrorCode()) ||
                e.getErrorCode().equalsIgnoreCase(PaymentCreditErrors.ERROR_BALANCE_BELOW_ZERO.getErrorCode());
    }

    private InstallmentErrorCustom getCustomErrors(List<GetAccountResponse> accounts,
                                                   CreditsV3Entity credit, String failure, String value) {
        InstallmentErrorCustom installmentErrorCustom = new InstallmentErrorCustom();
        List<CustomError> customErrors = new ArrayList<>();
        CustomError customError = new CustomError();
        customError.setFailure(failure);
        customError.setValue(value);
        customError.setDetail(getActualBalance(accounts, credit));
        customErrors.add(customError);
        installmentErrorCustom.setErrors(customErrors);
        return installmentErrorCustom;
    }

    private GetAccountRequest getAccountRequest(CreditsV3Entity credit) {
        GetAccountRequest accountRequest = new GetAccountRequest();
        accountRequest.setClientId(credit.getIdClientMambu());
        return accountRequest;
    }

    private String getActualBalance(List<GetAccountResponse> accounts, CreditsV3Entity creditsEntity) {
        return Optional.of(accounts)
                .filter(accountsFiltered -> Boolean.FALSE.equals(accountsFiltered.isEmpty()))
                .map(account -> account
                        .stream()
                        .filter(accountResponse -> accountResponse.getNumber().equalsIgnoreCase(creditsEntity.getIdSavingAccount()))
                        .collect(Collectors.toList())
                        .get(0)
                        .getBalance()
                        .getAmount()
                        .toString())
                .orElse("");
    }

    private void setLoanStatusClosedAndSave(CreditsV3Entity creditsEntity, PaymentInstallment request) {
        if (TRUE.equals(request.getPaidInFull())) {
            LoanStatusV3 loanStatus = Optional.ofNullable(creditsEntity.getLoanStatus()).orElse(new LoanStatusV3());
            loanStatus.setStatus(CLOSED.name());
            loanStatus.setCertificationSent(TRUE);
            creditsEntity.setLoanStatus(loanStatus);
            creditsEntity.setClosedDate(now());
            creditsEntity.setStatementsIndex(getStatementsIndex(creditsEntity));
        }
    }

    private String getStatementsIndex(CreditsV3Entity creditsEntity) {
        return Option.of(creditsEntity.getStatementsIndex())
                .map(index -> index.replace(APPROVED.name(), CLOSED.name()))
                .getOrElse(EMPTY);
    }

    private InstallmentPaid getInstallmentPaid(PaymentResponse paymentResponse, CreditsV3Entity creditsEntity) {
        return InstallmentPaid.builder()
                .idCredit(creditsEntity.getIdCredit().toString())
                .idClient(creditsEntity.getIdClient())
                .idLoan(creditsEntity.getIdLoanAccountMambu())
                .closedDate(creditsEntity.getClosedDate())
                .acceptDate(creditsEntity.getAcceptDate())
                .amountOffer(getAmountOffer(creditsEntity))
                .status(paymentResponse.getStatus())
                .build();
    }

    private BigDecimal getAmountOffer(CreditsV3Entity creditsEntity) {
        return Optional.ofNullable(
                creditsEntity.getAcceptOffer())
                .map(offer -> BigDecimal.valueOf(offer.getAmount()))
                .orElse(null);
    }
}
