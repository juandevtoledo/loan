package com.lulobank.credits.starter.v3.adapters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.lulobank.credits.starter.v3.adapters.out.productoffer.ProductOfferNotificationServiceSqsAdapter;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferNotificationService;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;

@Configuration
@Import({RestTemplateClientConfig.class})
public class ProductOfferConfig {

    @Value("${cloud.aws.sqs.queue.client-events-v2}")
    private String clientsSqsEndpointV2;
    
    @Bean()
    public ProductOfferNotificationService getNewProductOfferNotificationService(SqsBraveTemplate sqsBraveTemplate) {
        return new ProductOfferNotificationServiceSqsAdapter(sqsBraveTemplate, clientsSqsEndpointV2);
    }
}
