package com.lulobank.credits.starter.v3.adapters.in.dto.loan;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentPlan {
    private final LocalDateTime dueDate;
    private final String state;
    private final BigDecimal principalDue;
    private final BigDecimal totalDue;
    private final BigDecimal penaltyDue;
    private final BigDecimal insuranceFee;
}