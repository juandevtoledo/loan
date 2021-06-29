package com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum;

import java.util.Arrays;

public enum PaymentType {
    LOAN_REPAYMENT,
    LOAN_REPAYMENT_PARTIALLY_PAID,
    LOAN_REPAYMENT_EXTRA_AMOUNT;

    public static boolean isValidType(String value) {
        return Arrays.stream(PaymentType.values())
                .anyMatch(paymentType -> paymentType.name().equals(value));
    }
}
