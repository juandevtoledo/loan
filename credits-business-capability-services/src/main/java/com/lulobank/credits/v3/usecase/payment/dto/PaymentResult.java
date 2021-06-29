package com.lulobank.credits.v3.usecase.payment.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Builder
public class PaymentResult {

    private final BigDecimal amountPaid;
    private final LocalDateTime date;
    private final String transactionId;

}
