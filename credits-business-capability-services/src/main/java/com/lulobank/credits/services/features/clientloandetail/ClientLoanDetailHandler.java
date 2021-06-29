package com.lulobank.credits.services.features.clientloandetail;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.credits.sdk.dto.clientloandetail.GetClientLoan;
import com.lulobank.credits.sdk.dto.clientloandetail.Loan;
import com.lulobank.credits.sdk.dto.clientloandetail.Payment;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.PaymentType;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.LoanStatus;
import com.lulobank.credits.services.outboundadapters.repository.CreditsRepository;
import com.lulobank.credits.v3.service.NextInstallmentsService;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.response.GetLoanResponse;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.lulobank.credits.services.outboundadapters.flexibility.FlexibilityMapper.getLoanMovementsRequest;
import static com.lulobank.credits.services.outboundadapters.flexibility.FlexibilityMapper.getLoanRequestFromCreditEntityInfo;
import static com.lulobank.credits.v3.service.mapper.LoanDetailMapper.toLoanDetail;

@RequiredArgsConstructor
@CustomLog
public class ClientLoanDetailHandler implements Handler<Response<List<Loan>>, GetClientLoan> {

    private final FlexibilitySdk coreBankingSdk;
    private final CreditsRepository repository;
    private final NextInstallmentsService nextInstallmentsService;

    private static final long MAX_NUMBER_OF_LOAN_MOVEMENTS = 5;


    @Override
    public Response<List<Loan>> handle(GetClientLoan getClientLoan) {
        return Option.ofOptional(findOpenLoansByClient(getClientLoan))
                .toTry()
                .flatMap(this::getLoan)
                .flatMap(this::saveLoanStatus)
                .map(loan -> new Response<>(Collections.singletonList(loan)))
                .onFailure(error -> log.error("Error try to build Loan Information by idClient : {} , msg : {} ", getClientLoan.getIdClient(), error.getMessage(), error))
                .recover(error -> new Response<>(Collections.<Loan>emptyList()))
                .get();
    }


    private Try<Loan> getLoan(CreditsEntity creditsEntity) {
        return Try.of(() -> coreBankingSdk.getLoanByLoanAccountId(getLoanRequestFromCreditEntityInfo(creditsEntity)))
                .mapTry(getLoanResponse -> buildLoan(creditsEntity, getLoanResponse))
                .onFailure(error -> log.error("Error in coreBanking , service GetLoanAccountId , message : {} ", error.getMessage(), error));
    }

    private Loan buildLoan(CreditsEntity creditsEntity, GetLoanResponse getLoanResponse) throws ProviderException {
        Loan loan = new Loan();
        List<Payment> payments = getPaymentList(creditsEntity);
        loan.setNextInstallment(nextInstallmentsService.get(toLoanDetail(getLoanResponse)));
        loan.setPaymentList(payments.stream().limit(MAX_NUMBER_OF_LOAN_MOVEMENTS).collect(Collectors.toList()));
        loan.setLoanDetail(ClientLoanDetailMapper.INSTANCE.creditsEntityToLoanDetail(creditsEntity, getLoanResponse, payments, getLoanResponse.getAccountState()));
        loan.setClosedDate(getLoanResponse.getLastModifiedDate());
        return loan;
    }


    private List<Payment> getPaymentList(CreditsEntity creditsEntity) throws ProviderException {
        return coreBankingSdk.getLoanMovements(getLoanMovementsRequest(creditsEntity))
                .getLoanMovementList()
                .stream()
                .filter(x->PaymentType.isValidType(x.getTransactionType()))
                .map(LoanMovementMapper.INSTANCE::paymentTo)
                .collect(Collectors.toList());
    }

    private Try<Loan> saveLoanStatus(Loan loan) {
        return Option.ofOptional(repository.findByIdCredit(UUID.fromString(loan.getLoanDetail().getIdCredit())))
                .toTry()
                .map(creditsEntity -> setLoanStatusInEntity(loan, creditsEntity))
                .flatMap(creditsEntity -> Try.run(() -> repository.save(creditsEntity)))
                .map(success -> loan);
    }

    private CreditsEntity setLoanStatusInEntity(Loan loan, CreditsEntity creditsEntity) {
        LoanStatus loanStatus = new LoanStatus();
        loanStatus.setCertificationSent(false);
        loanStatus.setStatus(loan.getLoanDetail().getState());
        creditsEntity.setClosedDate(loan.getClosedDate());
        creditsEntity.setLoanStatus(loanStatus);
        return creditsEntity;
    }

    private Optional<CreditsEntity> findOpenLoansByClient(GetClientLoan getClientLoan) {
        return repository.findByidClientAndLoanStatusIsNotAndAcceptDateNotNull
                (getClientLoan.getIdClient(), getLoanStatusClosed())
                .stream()
                .findFirst();
    }

    private LoanStatus getLoanStatusClosed() {
        LoanStatus loanStatus = new LoanStatus();
        loanStatus.setStatus(CbsLoanStateEnum.CLOSED.name());
        loanStatus.setCertificationSent(true);
        return loanStatus;
    }


}
