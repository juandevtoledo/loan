package com.lulobank.credits.starter.v3.adapters.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.service.RiskEngineResultValidationsService;

@Configuration
public class RiskEngineResultValidationsServiceConfig {

	@Bean
	public RiskEngineResultValidationsService getRiskEngineResultValidationsService(CreditsV3Repository creditsV3Repository) {
		return new RiskEngineResultValidationsService(creditsV3Repository);
	}
}
