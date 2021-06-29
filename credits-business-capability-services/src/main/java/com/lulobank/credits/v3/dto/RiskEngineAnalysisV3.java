package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiskEngineAnalysisV3 {
    private Double amount;
    private Float interestRate;
    private Integer installments;
    private Double maxAmountInstallment;
    private String type;
}