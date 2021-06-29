package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.v3.dto.CreditsConditionV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class CreditsConditionV3Config {
    @Value("${creditscondition.minOfferAmount}")
    private Double minOfferAmount;

    @Value("${creditscondition.loanProductId}")
    private String loanProductId;

    @Value("${creditscondition.defaultCurrency}")
    private String defaultCurrency;

    @Value("${creditscondition.insuranceCost}")
    private Double insuranceCost;

    @Value("${creditscondition.cbsProductKeyType}")
    private String cbsProductKeyType;

    @Value("${creditscondition.feeInsurance}")
    private Double feeInsurance;

    @Value("${creditscondition.feeAmountInstallement}")
    private Double feeAmountInstallement;

    @Value("${creditscondition.minDayToPaymin}")
    private Integer minDayToPaymin;

    @Bean
    public CreditsConditionV3 creditsConditionV3(){
        CreditsConditionV3 creditsConditionV3 = new CreditsConditionV3();
        creditsConditionV3.setMinOfferAmount(minOfferAmount);
        creditsConditionV3.setLoanProductId(loanProductId);
        creditsConditionV3.setDefaultCurrency(defaultCurrency);
        creditsConditionV3.setInsuranceCost(insuranceCost);
        creditsConditionV3.setCbsProductKeyType(cbsProductKeyType);
        creditsConditionV3.setFeeInsurance(feeInsurance);
        creditsConditionV3.setFeeAmountInstallment(feeAmountInstallement);
        creditsConditionV3.setMinDayToPaymin(minDayToPaymin);
        return creditsConditionV3;
    }
}
