package com.lulobank.credits.starter.config;

import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.credits.sdk.operations.impl.PendingValidationsOperations;
import com.lulobank.credits.services.features.services.PendingValidationsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientServiceConfig {

    @Value("${services.clients.url}")
    private String serviceDomain;
    @Bean
    public PendingValidationsService pendingValidationsService(){
        return new PendingValidationsService(new PendingValidationsOperations(serviceDomain));
    }

    @Bean
    public RetrofitClientOperations retrofitClientOperations(){
        return new RetrofitClientOperations(serviceDomain);
    }
}
