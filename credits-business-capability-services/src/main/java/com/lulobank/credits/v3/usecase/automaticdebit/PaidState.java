package com.lulobank.credits.v3.usecase.automaticdebit;

import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerNotificationAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.service.AutomaticDebitPaymentService;
import com.lulobank.credits.v3.usecase.automaticdebit.command.ProcessPayment;
import lombok.CustomLog;

import java.time.LocalDate;
import java.util.Arrays;

import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum.ACTIVE;
import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper.SchedulerAutomaticDebitMapper.createOneTimeTransactionRequest;
import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper.SchedulerAutomaticDebitMapper.transactionRequest;
import static java.math.BigDecimal.ZERO;

@CustomLog
public enum PaidState {

    PAID() {
        @Override
        boolean valid(LoanInformation loanInformation) {
            return ZERO.compareTo(loanInformation.getInstallmentExpectedDue().getValue()) == 0;
        }

        @Override
        void action(ProcessPayment processPayment,
                    AutomaticDebitPaymentService automaticDebitPaymentService, SchedulerTransactionAsyncService schedulerAsyncService,
                    SchedulerNotificationAsyncService schedulerNotificationAsyncService) {
            log.info("Installment was Paid by idClient {} ", processPayment.getIdClient());
            schedulerNotificationAsyncService.successNotification(transactionRequest(processPayment));
        }
    },
    NO_ACTIVE() {
        @Override
        boolean valid(LoanInformation loanInformation) {
            return !(ACTIVE_IN_ARREARS.name().equals(loanInformation.getState()) || ACTIVE.name().equals(loanInformation.getState()));
        }

        @Override
        void action(ProcessPayment processPayment,
                    AutomaticDebitPaymentService automaticDebitPaymentService, SchedulerTransactionAsyncService schedulerAsyncService,
                    SchedulerNotificationAsyncService schedulerNotificationAsyncService) {
            log.info("Loan State is  Not Active , by idClient {} ", processPayment.getIdClient());
            schedulerNotificationAsyncService.failedNotification(transactionRequest(processPayment));
            schedulerAsyncService.retryTransaction(transactionRequest(processPayment));
        }
    },
    ACTIVE_IN_ARREARS() {
        @Override
        boolean valid(LoanInformation loanInformation) {
            return ACTIVE_IN_ARREARS.name().equals(loanInformation.getState());
        }

        @Override
        void action(ProcessPayment processPayment, AutomaticDebitPaymentService automaticDebitPaymentService, SchedulerTransactionAsyncService schedulerAsyncService,
                    SchedulerNotificationAsyncService schedulerNotificationAsyncService) {
            automaticDebitPaymentService.payment(processPayment)
            .onSuccess(success -> schedulerNotificationAsyncService.successNotification(transactionRequest(processPayment)));
        }
    },
    DAY_NO_PAID() {
        @Override
        boolean valid(LoanInformation loanInformation) {
            return LocalDate.now().isBefore(loanInformation.getInstallmentDate().toLocalDate());

        }

        @Override
        void action(ProcessPayment processPayment,
                    AutomaticDebitPaymentService automaticDebitPaymentService, SchedulerTransactionAsyncService schedulerAsyncService,
                    SchedulerNotificationAsyncService schedulerNotificationAsyncService) {
            log.info("Payment is not today, idClient {}  ", processPayment.getIdClient());
            schedulerAsyncService.oneTimeNotification(createOneTimeTransactionRequest(processPayment, processPayment.getLoanInformation().getInstallmentDate().getDayOfMonth()));
        }
    },
    READY_TO_PAID() {
        @Override
        boolean valid(LoanInformation loanInformation) {
            return LocalDate.now().equals(loanInformation.getInstallmentDate().toLocalDate());
        }

        @Override
        void action(ProcessPayment processPayment,
                    AutomaticDebitPaymentService automaticDebitPaymentService, SchedulerTransactionAsyncService schedulerAsyncService,
                    SchedulerNotificationAsyncService schedulerNotificationAsyncService) {
            automaticDebitPaymentService.payment(processPayment)
            .onSuccess(success -> schedulerNotificationAsyncService.successNotification(transactionRequest(processPayment)));
        }
    },
    NONE() {
        @Override
        boolean valid(LoanInformation loanInformation) {
            return true;
        }

        @Override
        void action(ProcessPayment processPayment,
                    AutomaticDebitPaymentService automaticDebitPaymentService, SchedulerTransactionAsyncService schedulerAsyncService,
                    SchedulerNotificationAsyncService schedulerNotificationAsyncService) {
            log.info("Not condition has been met, idClient : ", processPayment.getIdClient());
            schedulerNotificationAsyncService.failedNotification(transactionRequest(processPayment));
        }
    };

    abstract boolean valid(LoanInformation loanInformation);

    abstract void action(ProcessPayment processPayment, AutomaticDebitPaymentService automaticDebitPaymentService,
                         SchedulerTransactionAsyncService schedulerAsyncService, SchedulerNotificationAsyncService schedulerNotificationAsyncService
    );

    public static PaidState find(LoanInformation loanInformation) {
        return Arrays.stream(PaidState.values())
                .filter(paidState -> paidState.valid(loanInformation))
                .findFirst()
                .orElse(NONE);
    }


}
