package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditsConditionV3 {
    private Double minOfferAmount;
    private Double insuranceCost;
    private String loanProductId;
    private String defaultCurrency;
    private String cbsProductKeyType;
    private Double feeInsurance;
    private Double feeAmountInstallment;
    private Integer minDayToPaymin;

}
