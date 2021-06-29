package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum.PARTIALLY_PAID;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum.PENDING;
import static java.math.BigDecimal.ZERO;

@Builder
@Getter
public class LoanInformation {
    private final AmountCurrency installmentExpectedDue;
    private final AmountCurrency totalBalance;
    private final AmountCurrency loanAmount;
    private final AmountCurrency installmentAccruedDue;
    private final Rates rates;
    private final String loanId;
    private final String state;
    private final LocalDateTime creationOn;
    private final LocalDateTime cutOffDate;
    private final LocalDateTime installmentDate;
    private final List<PaymentPlan> paymentPlanList;
    private final boolean automaticDebit;
    private final AmountCurrency installmentExpected;
    private final AmountCurrency installmentAccrued;

    public boolean isLastInstallment() {
        return countPendingStatements() <= 1L;
    }

    private long countPendingStatements() {
        return paymentPlanList.stream()
                .filter(this::paymentIsPending)
                .count();
    }

    private boolean paymentIsPending(PaymentPlan paymentPlan) {
        return PENDING.name().equals(paymentPlan.getState()) ||
               PARTIALLY_PAID.name().equals(paymentPlan.getState());
    }

    public boolean doesCustomerOweMoney(){
        return ZERO.compareTo(installmentExpected.getValue()) < 0;
    }

    public boolean isMinimumPaymentActive(){
        return !isLastInstallment() && doesCustomerOweMoney();
    }
}
