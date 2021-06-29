package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InitialOfferV3 {

    private Double minAmount;
    private Double maxAmount;
    private Double maxAmountInstallment;
    private Float interestRate;
    private String typeOffer;
    private RiskEngineAnalysisV3 riskEngineAnalysis;
    private Double amount;
    private List<OfferEntityV3> offerEntities;
    private LocalDateTime generateDate;
    private BigDecimal clientLoanRequestedAmount;
    private List<RiskResult> results;

    public InitialOfferV3(Double minAmount, Double maxAmount, Float interestRate, Double maxAmountInstallment) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
        this.maxAmountInstallment = maxAmountInstallment;
    }
}