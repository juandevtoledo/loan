package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanConditionsEntityV3 {

    private Double amount;
    private Float interestRate;
    private Float defaultRate;
    private Integer installments;
    private Double maxAmountInstallment;
    private String type;
}
