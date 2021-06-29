package com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum;

import java.util.stream.Stream;

public enum FlexibilityInstallmentPendingStateEnum {
    LATE, PENDING, PARTIALLY_PAID, RESCHEDULED, GRACE,
    ;

    public static boolean stateIsNotPaid (String state){
        return Stream.of(state)
                .anyMatch(installmentsState -> LATE.name().equals(installmentsState));
    }

}
