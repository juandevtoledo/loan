package com.lulobank.credits.starter.v3.adapters.out.sqs.pse.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoanClosedByPSETotalPaymentMessage{
    private final String idClient;
    private final String productTransaction;
}
