package com.lulobank.credits.v3.port.out.corebanking.dto;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentPlan {
    private final LocalDateTime dueDate;
    private final LocalDateTime cutOffDate;
    private final String state;
    private final BigDecimal principalDue;
    private final BigDecimal totalDue;
    private final BigDecimal penaltyDue;
    private final BigDecimal insuranceFee;
    private final BigDecimal feesDue;
    private final BigDecimal interestDue;
}
