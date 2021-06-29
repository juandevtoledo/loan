package com.lulobank.credits.starter.config;

import com.lulobank.credits.services.domain.CreditsConditionDomain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreditsConditionConfig {

    @Value("${creditscondition.feeAmountInstallement}")
    private Double feeAmountInstallement;

    @Value("${creditscondition.minDayToPaymin}")
    private Integer minDayToPaymin;

    @Bean
    public CreditsConditionDomain creditsConfigDomain(){
        CreditsConditionDomain creditsConditionDomain = new CreditsConditionDomain();
        creditsConditionDomain.setFeeAmountInstallement(feeAmountInstallement);
        creditsConditionDomain.setMinimumPayDay(minDayToPaymin);
        return creditsConditionDomain;
    }

}
