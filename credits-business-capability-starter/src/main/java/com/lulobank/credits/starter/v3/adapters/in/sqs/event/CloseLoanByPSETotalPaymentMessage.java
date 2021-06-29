package com.lulobank.credits.starter.v3.adapters.in.sqs.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloseLoanByPSETotalPaymentMessage  {
    private String idClient;
    private String productTransaction;
}
