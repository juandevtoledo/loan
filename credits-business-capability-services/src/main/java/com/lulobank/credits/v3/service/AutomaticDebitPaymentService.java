package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.service.dto.LoanPaymentRequest;
import com.lulobank.credits.v3.usecase.automaticdebit.command.ProcessPayment;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper.SchedulerAutomaticDebitMapper.transactionRequest;

@CustomLog
@RequiredArgsConstructor
public class AutomaticDebitPaymentService {

    private final LoanPaymentService loanPaymentService;
    private final CloseLoanService closeLoanService;
    private final SchedulerTransactionAsyncService schedulerAsyncService;
    private final FailurePaymentService failurePaymentService;

    public Try<ProcessPayment> payment(ProcessPayment processPayment) {
        return loanPaymentService.makePayment(getLoanPaymentRequest(processPayment))
                .peekLeft(error -> log.error("Error making payment , Retry , BusinessCode {} , ProviderCode {} , idClient {} ",
                        error.getBusinessCode(), error.getProviderCode(), processPayment.getIdClient()))
                .fold(error -> failurePaymentService.retry(processPayment), success -> successPayment(processPayment))
                .onFailure(error -> log.error("Error in process payment , msg {}, idClient {}  ", error.getMessage(), processPayment.getIdClient()));
    }

    private Try<ProcessPayment> successPayment(ProcessPayment processPayment) {
        return processLastInstallment(processPayment)
              .andFinally(() -> deleteOneTimeTransaction(processPayment));
    }

    private Try<ProcessPayment> processLastInstallment(ProcessPayment processPayment) {
        return processPayment.getLoanInformation().isLastInstallment() ? closeLoan(processPayment) : Try.of(() -> processPayment);
    }

    private Try<ProcessPayment> closeLoan(ProcessPayment processPayment) {
        return closeLoanService.close(processPayment.getIdCredit(), processPayment.getIdLoanAccountMambu())
                .toTry()
                .map(creditsV3Entity -> processPayment)
                .onFailure(error -> log.info("Failed payment loan , message {}  idClient {}, ", error.getMessage(), processPayment.getIdClient(), error))
                .onFailure(failure -> schedulerAsyncService.retryTransaction(transactionRequest(processPayment)));
    }

    private LoanPaymentRequest getLoanPaymentRequest(ProcessPayment processPayment) {
        return LoanPaymentRequest.builder()
                .amount(processPayment.getLoanInformation().getInstallmentExpectedDue().getValue())
                .loanId(processPayment.getIdLoanAccountMambu())
                .idClient(processPayment.getIdClient())
                .idCredit(processPayment.getIdCredit())
                .paymentOff(processPayment.getLoanInformation().isLastInstallment())
                .build();
    }

    private void deleteOneTimeTransaction(ProcessPayment processPayment) {
        if (processPayment.isOneTime())
            schedulerAsyncService.deleteTransaction(transactionRequest(processPayment));
    }
}
