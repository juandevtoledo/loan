package com.lulobank.credits.v3.usecase.movement.dto;

import java.util.Arrays;

public enum MovementType {
    LOAN_REPAYMENT,
    LOAN_REPAYMENT_PARTIALLY_PAID,
    LOAN_REPAYMENT_EXTRA_AMOUNT,
    LOAN_REPAYMENT_TOTAL;

    public static boolean isValidType(String value) {
        return Arrays.stream(MovementType.values())
                .anyMatch(movementType -> movementType.name().equals(value));
    }
}
