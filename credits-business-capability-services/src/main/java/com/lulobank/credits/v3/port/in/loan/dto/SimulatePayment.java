package com.lulobank.credits.v3.port.in.loan.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class SimulatePayment {
    private LocalDateTime dueDate;
    private BigDecimal totalDue;
    private BigDecimal feesDue;
    private BigDecimal interestDue;
    private BigDecimal principalDue;

}
