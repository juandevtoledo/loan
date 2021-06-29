package com.lulobank.credits.v3.usecase.closeloan.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientWithExternalPayment{
    private final String idClient;
    private final String productTransaction;
}
