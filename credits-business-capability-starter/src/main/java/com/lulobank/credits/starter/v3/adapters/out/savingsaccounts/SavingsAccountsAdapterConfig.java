package com.lulobank.credits.starter.v3.adapters.out.savingsaccounts;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.lulobank.credits.starter.v3.adapters.config.RestTemplateClientConfig;
import com.lulobank.credits.v3.port.in.savingsaccount.SavingsAccountV3Service;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;

@Configuration
@Import({RestTemplateClientConfig.class})
public class SavingsAccountsAdapterConfig {
	
    @Bean
    public SavingsAccountV3Service savingsAccountV3Service(@Qualifier("savingsRestTemplate") RestTemplateClient savingsRestTemplate) {
        return new SavingsAccountsV3ServiceAdapter(savingsRestTemplate);
    }

}

