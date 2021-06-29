package com.lulobank.credits.v3.usecase.installment.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class CalculateExtraAmountInstallment {

    private final String idCredit;
    private final BigDecimal amount;
}
