package com.lulobank.credits.v3.port.in.nextinstallment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Flag {
    private final boolean payNow;
    private final boolean minimumPaymentActive;
    private final boolean automaticDebitActive;
    private final boolean customerOweMoney;

    public static Flag empty(){
        return Flag.builder().build();
    }
}
