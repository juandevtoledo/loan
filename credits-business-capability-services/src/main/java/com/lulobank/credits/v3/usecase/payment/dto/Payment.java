package com.lulobank.credits.v3.usecase.payment.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Payment {
    private final String clientId;
    private final String creditId;
    private final BigDecimal amount;
    private final PaymentType paymentType;
    private final SubPaymentType subPaymentType;
}
