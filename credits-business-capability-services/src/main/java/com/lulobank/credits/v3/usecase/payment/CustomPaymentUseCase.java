package com.lulobank.credits.v3.usecase.payment;

import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.service.dto.LoanPaymentRequest;
import com.lulobank.credits.v3.usecase.payment.command.CustomPaymentInstallment;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class CustomPaymentUseCase implements UseCase<CustomPaymentInstallment, Either<UseCaseResponseError, Boolean>> {

    private final LoanPaymentService loanPaymentService;

    @Override
    public Either<UseCaseResponseError, Boolean> execute(CustomPaymentInstallment command) {
        log.info("Process payment Custom , idClient : {}, amount : {} , loanId : {} ", command.getCreditId(), command.getCreditId());
        return loanPaymentService.makePayment(getLoanPaymentRequest(command))
                .peek(paymentResponse -> log.info("Payment success Custom to idClient : {} , amoun : {} ", command.getClientId(), paymentResponse.getStatus()))
                .peekLeft(error -> log.error("Error process payment Custom, businessCode : {} , providerCode : {} , idClient : {} ", error.getBusinessCode(), error.getProviderCode(), command.getClientId()))
                .map(success -> true);
    }

    private LoanPaymentRequest getLoanPaymentRequest(CustomPaymentInstallment command) {
        return LoanPaymentRequest.builder()
                .amount(command.getAmount())
                .loanId(command.getCoreCbsId())
                .idClient(command.getClientId())
                .idCredit(command.getCreditId())
                .paymentOff(false)
                .type(command.getType())
                .build();
    }
}
