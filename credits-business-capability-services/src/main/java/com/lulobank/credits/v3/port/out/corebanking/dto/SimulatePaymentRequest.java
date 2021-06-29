package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SimulatePaymentRequest {
    private final BigDecimal amount;
    private final Integer installment;
    private final Integer dayOfPay;
    private final BigDecimal interestRate;
}