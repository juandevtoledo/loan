package com.lulobank.credits.starter.v3.adapters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;

public class RestTemplateClientConfig {
	
	@Value("${services.savings.url}")
    private String savingsDomain;
	
	@Value("${services.clients.url}")
    private String clientsDomain;
	
	@Bean("savingsRestTemplate")
	public RestTemplateClient getSavingsRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return new RestTemplateClient(savingsDomain, restTemplateBuilder);
	}
	
	@Bean("clientsRestTemplate")
	public RestTemplateClient getClientsRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return new RestTemplateClient(clientsDomain, restTemplateBuilder);
	}

}
