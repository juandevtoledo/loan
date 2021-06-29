package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class SimulatePayment {
    private final LocalDateTime dueDate;
    private final BigDecimal totalDue;
    private final BigDecimal feesDue;
    private final BigDecimal interestDue;
    private final BigDecimal principalDue;

}