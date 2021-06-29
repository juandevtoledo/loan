package com.lulobank.credits.v3.usecase.payment;

import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.service.dto.LoanPaymentRequest;
import com.lulobank.credits.v3.usecase.payment.command.MinPaymentInstallment;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class MinimumPaymentUseCase implements UseCase<MinPaymentInstallment, Either<UseCaseResponseError, Boolean>> {

    private final LoanPaymentService loanPaymentService;

    @Override
    public Either<UseCaseResponseError, Boolean> execute(MinPaymentInstallment command) {
        log.info("Process payment , idClient : {}, amount : {} , loanId : {} ", command.getCreditId(), command.getCreditId());
        return loanPaymentService.makePayment(getLoanPaymentRequest(command))
                .peek(paymentResponse -> log.info("Payment success to idClient : {} , amoun : {} ", command.getClientId(), paymentResponse.getStatus()))
                .peekLeft(error -> log.error("Error process payment , businessCode : {} , providerCode : {} , idClient : {} ", error.getBusinessCode(), error.getProviderCode(), command.getClientId()))
                .map(success -> true);
    }

    private LoanPaymentRequest getLoanPaymentRequest(MinPaymentInstallment command) {
        return LoanPaymentRequest.builder()
                .amount(command.getAmount())
                .loanId(command.getCoreCbsId())
                .idClient(command.getClientId())
                .idCredit(command.getCreditId())
                .paymentOff(false)
                .build();
    }


}
