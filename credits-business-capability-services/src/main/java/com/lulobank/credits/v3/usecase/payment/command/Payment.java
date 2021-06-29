package com.lulobank.credits.v3.usecase.payment.command;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@SuperBuilder
public class Payment {
    private final String clientId;
    private final String creditId;
    private final String coreCbsId;
    private final BigDecimal amount;
}
