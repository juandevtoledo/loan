package com.lulobank.credits.v3.usecase.payment.command;

import com.lulobank.credits.v3.vo.AdapterCredentials;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class TotalPaymentInstallment extends Payment{
    private final AdapterCredentials adapterCredentials;
}
