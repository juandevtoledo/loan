package com.lulobank.credits.services.features.riskmodelscore.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeadCondition {
    private Double amount;
    private Float interestRate;
    private Integer installments;
    private Double maxAmountInstallment;
    private String type;

    public LeadCondition(Double amount, Float interestRate,  Integer installments, Double maxAmountInstallment, String type) {
        this.amount = amount;
        this.interestRate = interestRate;
        this.installments = installments;
        this.maxAmountInstallment = maxAmountInstallment;
        this.type = type;
    }
}
