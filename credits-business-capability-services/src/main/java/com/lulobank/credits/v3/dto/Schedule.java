package com.lulobank.credits.v3.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Schedule {
    private final Integer installment;
    private final BigDecimal interestRate;
    private final BigDecimal annualNominalRate;
    private final BigDecimal monthlyNominalRate;
}
