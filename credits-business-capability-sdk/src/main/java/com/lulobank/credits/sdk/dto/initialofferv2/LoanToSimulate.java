package com.lulobank.credits.sdk.dto.initialofferv2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoanToSimulate {

    private Double insurance;
    private Double amount;
    private Integer installment;
    private Double interestRate;
    private Double interestRateTem;
    private Double interest;
    private Double feeInsurance;
    private RiskEngineAnalysis riskEngineAnalysis;
}
