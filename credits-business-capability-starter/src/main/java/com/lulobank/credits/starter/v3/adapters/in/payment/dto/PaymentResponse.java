package com.lulobank.credits.starter.v3.adapters.in.payment.dto;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse extends AdapterResponse {
    private final BigDecimal amountPaid;
    private final LocalDateTime date;
    private final String transactionId;
}
