package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.ClientAccount;
import com.lulobank.credits.v3.port.out.corebanking.dto.CreatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.port.out.corebanking.dto.TypePayment;
import com.lulobank.credits.v3.service.dto.LoanPaymentRequest;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

import static com.lulobank.credits.services.utils.SavingAccountState.LOCKED;

@CustomLog
@RequiredArgsConstructor
public class LoanPaymentService {

    private final CoreBankingService coreBankingService;
    private final CreditsV3Repository creditsV3Repository;

    public Either<UseCaseResponseError, PaymentApplied> makePayment(LoanPaymentRequest loanPaymentRequest) {
        return creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(loanPaymentRequest.getIdCredit(), loanPaymentRequest.getLoanId())
                .filter(creditsV3Entity -> creditsV3Entity.getIdClient().equals(loanPaymentRequest.getIdClient()))
                .toEither(CreditsError::databaseError)
                .mapLeft(UseCaseResponseError::map)
                .flatMap(creditsV3Entity -> processByClient(creditsV3Entity, loanPaymentRequest))
                .mapLeft(creditsError -> creditsError);

    }

    private Either<UseCaseResponseError, PaymentApplied> processByClient(CreditsV3Entity creditsV3Entity, LoanPaymentRequest loanPaymentRequest) {
        return coreBankingService.getAccountsByClient(creditsV3Entity.getIdClientMambu())
                .peekLeft(error -> log.error("Error finding accountsActive , providerCode , businessCode {} ", error.getProviderCode(), error.getBusinessCode()))
                .filter(accountActive(creditsV3Entity))
                .toEither(CoreBankingError.accountBlocked())
                .flatMap(accountsActive ->
                        accountsActive
                        .flatMap(account -> coreBankingService.payment(getLoanPaymentRequest(creditsV3Entity, loanPaymentRequest)))
                )
                .peek(response -> log.info("Payment success : Amount : {} , status : {} ", response.getAmount(), response.getStatus()))
                .peekLeft(error -> log.error("Payment failed , providerCode , businessCode {} ", error.getProviderCode(), error.getBusinessCode()))
                .mapLeft(UseCaseResponseError::map);
    }

    private Predicate<List<ClientAccount>> accountActive(CreditsV3Entity creditsV3Entity) {
        return accounts ->
                accounts.stream()
                        .filter(account -> creditsV3Entity.getIdSavingAccount().equals(account.getNumber()))
                        .noneMatch(a -> LOCKED.name().equals(a.getStatus()));
    }

    private CreatePayment getLoanPaymentRequest(CreditsV3Entity creditsV3Entity, LoanPaymentRequest loanPaymentRequest) {
        return CreatePayment.builder()
                .coreBankingId(creditsV3Entity.getIdClientMambu())
                .amount(loanPaymentRequest.getAmount())
                .accountId(creditsV3Entity.getIdSavingAccount())
                .payOff(loanPaymentRequest.getPaymentOff())
                .loanId(creditsV3Entity.getIdLoanAccountMambu())
                .type(TypePayment.getByName(loanPaymentRequest.getType()))
                .build();
    }


}
