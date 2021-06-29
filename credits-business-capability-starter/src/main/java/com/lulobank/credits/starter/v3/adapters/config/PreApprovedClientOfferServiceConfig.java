package com.lulobank.credits.starter.v3.adapters.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.service.PreApprovedClientOfferService;

@Configuration
public class PreApprovedClientOfferServiceConfig {

	@Bean
	public PreApprovedClientOfferService getPreApprovedClientOfferService(CreditsV3Repository creditsV3Repository) {
		return new PreApprovedClientOfferService(creditsV3Repository);
	}
}
