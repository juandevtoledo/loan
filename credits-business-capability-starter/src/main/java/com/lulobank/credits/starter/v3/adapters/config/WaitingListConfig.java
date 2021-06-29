package com.lulobank.credits.starter.v3.adapters.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.lulobank.credits.starter.v3.adapters.out.clients.ClientServiceAdapter;
import com.lulobank.credits.starter.v3.adapters.out.pep.PepServiceAdapter;
import com.lulobank.credits.starter.v3.adapters.out.productoffer.ProductOfferServiceAdapter;
import com.lulobank.credits.starter.v3.adapters.out.savingsaccounts.SavingAccountServiceAdapter;
import com.lulobank.credits.starter.v3.adapters.out.sqs.riskengine.RiskEngineNotificationServiceSqsAdapter;
import com.lulobank.credits.starter.v3.handler.WaitingListHandler;
import com.lulobank.credits.v3.port.out.clients.ClientService;
import com.lulobank.credits.v3.port.out.pep.PepService;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferService;
import com.lulobank.credits.v3.port.out.queue.RiskEngineNotificationService;
import com.lulobank.credits.v3.port.out.saving.SavingAccountService;
import com.lulobank.credits.v3.usecase.waitinglist.AddToWaitingListUseCase;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import co.com.lulobank.tracing.sqs.SqsBraveTemplate;

@Configuration
@Import({RestTemplateClientConfig.class})
public class WaitingListConfig {
	
    @Value("${cloud.aws.sqs.queue.risk-engine}")
    private String riskEngineSqsEndpoint;

	@Bean
	public PepService getPepService(@Qualifier("clientsRestTemplate") RestTemplateClient clientsRestTemplate) {
		return new PepServiceAdapter(clientsRestTemplate);
	}
	
	@Bean
	public SavingAccountService getSavingAccountService(@Qualifier("savingsRestTemplate") RestTemplateClient savingsRestTemplate) {
		return new SavingAccountServiceAdapter(savingsRestTemplate);
	}
	
	@Bean
	public ProductOfferService getProductOfferService(@Qualifier("clientsRestTemplate") RestTemplateClient clientsRestTemplate) {
		return new ProductOfferServiceAdapter(clientsRestTemplate);
	}
	
	@Bean
	public ClientService getClientService(@Qualifier("clientsRestTemplate") RestTemplateClient clientsRestTemplate) {
		return new ClientServiceAdapter(clientsRestTemplate);
	}
	
	@Bean
	public RiskEngineNotificationService getRiskEngineNotificationService(SqsBraveTemplate sqsBraveTemplate) {
		return new RiskEngineNotificationServiceSqsAdapter(sqsBraveTemplate, riskEngineSqsEndpoint);
	}
	
	@Bean
	public AddToWaitingListUseCase getAddToWaitingListUseCase(BeanFactory beanFactory, ProductOfferService productOfferService,
			ClientService clientService, RiskEngineNotificationService riskEngineNotificationService) {
		return new AddToWaitingListUseCase(beanFactory, productOfferService,
				clientService, riskEngineNotificationService);
	}
	
	@Bean
	public WaitingListHandler getWaitingListHandler(AddToWaitingListUseCase addToWaitingListUseCase) {
		return new WaitingListHandler(addToWaitingListUseCase);
	}
}
