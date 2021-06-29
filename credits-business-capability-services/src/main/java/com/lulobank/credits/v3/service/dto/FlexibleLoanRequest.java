package com.lulobank.credits.v3.service.dto;

import com.lulobank.credits.sdk.dto.initialofferv2.RiskEngineAnalysis;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FlexibleLoanRequest {
    private final Double insurance;
    private final Double amount;
    private final Integer installment;
    private final Double interestRate;
    private final Double interestRateTem;
    private final Double interest;
    private final Double feeInsurance;
    private final RiskEngineAnalysis riskEngineAnalysis;
}
