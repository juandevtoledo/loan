package com.lulobank.credits.v3.usecase.payment.command;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CustomPaymentInstallment extends Payment{
    private final String type;
}
