package com.lulobank.credits.services.inboundadapters.model;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class LoanConditions {
    private Double amount;
    private Float interestRate;
    private Float defaultRate;
    private Integer installments;
    private Double maxAmountInstallment;
    private String type;
}