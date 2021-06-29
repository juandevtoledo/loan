package com.lulobank.credits.v3.util;

import com.lulobank.credits.v3.port.out.corebanking.dto.AmountCurrency;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;

import java.time.LocalDateTime;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;

public class LoanMockFactory {

    public static LoanInformation readyToPaid() {
        return LoanInformation.builder()
                .loanId("loan_id")
                .state("ACTIVE")
                .installmentExpectedDue(AmountCurrency.builder().value(TEN).build())
                .installmentDate(LocalDateTime.now())
                .build();
    }

    public static LoanInformation paid() {
        return LoanInformation.builder()
                .loanId("loan_id")
                .state("ACTIVE")
                .installmentExpectedDue(AmountCurrency.builder().value(ZERO).build())
                .installmentDate(LocalDateTime.now())
                .build();
    }

    public static LoanInformation inArrears() {
        return LoanInformation.builder()
                .loanId("loan_id")
                .state("IN_ARREARS")
                .installmentExpectedDue(AmountCurrency.builder().value(TEN).build())
                .installmentDate(LocalDateTime.now())
                .build();
    }

    public static LoanInformation notIsToday() {
        return LoanInformation.builder()
                .loanId("loan_id")
                .state("ACTIVE")
                .installmentExpectedDue(AmountCurrency.builder().value(TEN).build())
                .installmentDate(LocalDateTime.now().plusDays(2))
                .build();
    }

    public static LoanInformation activeArrears() {
        return LoanInformation.builder()
                .loanId("loan_id")
                .state("ACTIVE_IN_ARREARS")
                .installmentExpectedDue(AmountCurrency.builder().value(TEN).build())
                .installmentDate(LocalDateTime.now())
                .build();
    }
}
