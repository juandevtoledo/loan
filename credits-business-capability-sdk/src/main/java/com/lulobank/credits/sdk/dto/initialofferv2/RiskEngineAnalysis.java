package com.lulobank.credits.sdk.dto.initialofferv2;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class RiskEngineAnalysis {

    @NotNull(message = "Amount is null")
    private Double amount;
    @NotNull(message = "InterestRate is null")
    private Float interestRate;
    private Integer installments;
    @NotNull(message = "MaxAmountInstallment is null")
    private Double maxAmountInstallment;
    @NotEmpty(message = "Type is null or empty")
    private String type;
}
