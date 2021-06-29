package com.lulobank.credits.starter.v3.adapters.out.productoffer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.credits.starter.v3.adapters.out.productoffer.ProductOfferNotificationServiceSqsAdapter;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferNotificationService;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;

@Configuration
public class ProductOfferNotificationServiceCofig {
	
    @Value("${cloud.aws.sqs.queue.client-events-v2}")
    private String clientsSqsEndpointV2;

	@Bean
	public ProductOfferNotificationService getProductOfferNotificationService(SqsBraveTemplate sqsBraveTemplate) {
		return new ProductOfferNotificationServiceSqsAdapter(sqsBraveTemplate, clientsSqsEndpointV2);
	}
}
