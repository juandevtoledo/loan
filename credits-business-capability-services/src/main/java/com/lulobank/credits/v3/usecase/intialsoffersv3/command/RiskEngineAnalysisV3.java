package com.lulobank.credits.v3.usecase.intialsoffersv3.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RiskEngineAnalysisV3 {

    private final Double amount;
    private final Float interestRate;
    private final Integer installments;
    private final Double maxAmountInstallment;
    private final String type;
}