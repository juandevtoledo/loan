package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PaymentDetail {

    private final BigDecimal insuranceCost;
    private final BigDecimal capitalPayment;
    private final BigDecimal ratePayment;
    private final BigDecimal penaltyAmount;
}