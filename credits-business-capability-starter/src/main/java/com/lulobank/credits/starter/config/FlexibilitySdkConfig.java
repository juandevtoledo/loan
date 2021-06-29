package com.lulobank.credits.starter.config;

import flexibility.client.FlexibilitySdkFactory;
import flexibility.client.models.Credentials;
import flexibility.client.sdk.FlexibilitySdk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlexibilitySdkConfig {

    @Value("${flexibility.sdk.url}")
    private String url;

    @Value("${flexibility.sdk.clientId}")
    private String clientId;

    @Value("${flexibility.sdk.secret}")
    private String secret;

    @Bean
    public FlexibilitySdk flexibilitySdk() {
        Credentials credentials = new Credentials();
        credentials.setSecret(secret);
        credentials.setClientId(clientId);
        return new FlexibilitySdkFactory().getSdk(url, credentials);
    }
}