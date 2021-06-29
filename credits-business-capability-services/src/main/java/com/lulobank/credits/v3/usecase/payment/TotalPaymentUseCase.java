package com.lulobank.credits.v3.usecase.payment;

import com.lulobank.credits.v3.service.CloseLoanService;
import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.service.dto.LoanPaymentRequest;
import com.lulobank.credits.v3.usecase.payment.command.TotalPaymentInstallment;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class TotalPaymentUseCase implements UseCase<TotalPaymentInstallment, Either<UseCaseResponseError, Boolean>> {

    private final LoanPaymentService loanPaymentService;
    private final CloseLoanService closeLoanService;

    @Override
    public Either<UseCaseResponseError, Boolean> execute(TotalPaymentInstallment command) {
        log.info("Process Total , idClient : {}, amount : {} , loanId : {} ", command.getCreditId(), command.getCreditId());
        return loanPaymentService.makePayment(getLoanPaymentRequest(command))
                .peek(paymentResponse -> log.info("Payment success to idClient : {} , amoun : {} ", command.getClientId(), paymentResponse.getStatus()))
                .peekLeft(error -> log.error("Error process payment , businessCode : {} , providerCode : {} , idClient : {} ", error.getBusinessCode(), error.getProviderCode(), command.getClientId()))
                .flatMap(paymentApplied -> closeLoanService.close(command.getCreditId(), command.getCoreCbsId()))
                .map(success -> true);
    }

    private LoanPaymentRequest getLoanPaymentRequest(TotalPaymentInstallment command) {
        return LoanPaymentRequest.builder()
                .amount(command.getAmount())
                .loanId(command.getCoreCbsId())
                .idClient(command.getClientId())
                .idCredit(command.getCreditId())
                .paymentOff(true)
                .build();
    }

}
