package com.lulobank.credits.starter.v3.adapters.in.dto.nextinstallment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Flag {
    private final boolean payNow;
    private final boolean minimumPaymentActive;
    private final boolean automaticDebitActive;
    private final boolean customerOweMoney;
}
