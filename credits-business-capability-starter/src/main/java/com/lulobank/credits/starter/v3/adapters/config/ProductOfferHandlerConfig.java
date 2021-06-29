package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.handler.ProductOfferHandler;
import com.lulobank.credits.v3.port.in.productoffer.GenerateProductOfferPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductOfferHandlerConfig {

    @Bean
    public ProductOfferHandler productOfferHandler(GenerateProductOfferPort generateProductOfferPort) {
        return new ProductOfferHandler(generateProductOfferPort);
    }

}
