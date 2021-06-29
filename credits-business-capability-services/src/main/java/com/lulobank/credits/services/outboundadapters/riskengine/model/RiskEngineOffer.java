package com.lulobank.credits.services.outboundadapters.riskengine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiskEngineOffer {
    private Double amount;
    private Float interestRate;
    private Integer installments;
    private Double maxAmountInstallment;
    private String type;
}
