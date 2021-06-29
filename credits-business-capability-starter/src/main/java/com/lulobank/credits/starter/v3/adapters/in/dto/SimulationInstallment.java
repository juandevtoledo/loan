package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SimulationInstallment {
    private final BigDecimal monthlyNominalRate;
    private final Integer installment;
    private final BigDecimal amount;
    private final BigDecimal interestRate;
    
}