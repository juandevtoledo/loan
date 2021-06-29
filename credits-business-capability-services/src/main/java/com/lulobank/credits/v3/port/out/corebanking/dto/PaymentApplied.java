package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentApplied {
    private final String status;
    private final BigDecimal amount;
    private final LocalDateTime entryDate;
    private final String transactionId;
}
