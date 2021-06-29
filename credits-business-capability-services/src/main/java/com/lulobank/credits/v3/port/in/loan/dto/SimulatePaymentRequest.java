package com.lulobank.credits.v3.port.in.loan.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SimulatePaymentRequest {
    private Double amount;
    private Integer installment;
    private Integer dayOfPay;
    private BigDecimal interestRate;
}
