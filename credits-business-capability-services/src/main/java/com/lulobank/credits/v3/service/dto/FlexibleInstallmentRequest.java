package com.lulobank.credits.v3.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class FlexibleInstallmentRequest {
    private final Integer initialInstallment;
    private final Integer endInstallment;
    private final BigDecimal interestRate;
    private final BigDecimal loanAmount;
    private final Double feeInsurance;
    private final BigDecimal monthlyNominalRate;
    private final BigDecimal annualNominalRate;
}
