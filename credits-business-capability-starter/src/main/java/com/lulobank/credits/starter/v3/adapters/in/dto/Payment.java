package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class Payment {
    private Integer installment;
    private String state;
    private LocalDate dueDate;
    private LocalDate cutOffDate;
    private BigDecimal principalDue;
    private BigDecimal totalDue;
    private BigDecimal interestDue;
    private BigDecimal feesDue;
    private BigDecimal penaltyDue;
    private BigDecimal pendingBalance;
    private Float percentPrincipalDue;
    private Float percentInterestDue;
    private Float percentPenaltyDue;
    private Float percentFeesDue;
}