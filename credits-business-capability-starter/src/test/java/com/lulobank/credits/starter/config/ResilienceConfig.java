package com.lulobank.credits.starter.config;

import flexibility.client.connector.ProviderException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.internal.InMemoryCircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slowCallDurationThreshold(Duration.ofSeconds(1))
                .slowCallRateThreshold(50)
                .slidingWindowSize(2)
                .minimumNumberOfCalls(1)
                .ignoreExceptions(ProviderException.class)
                .build();

        return new InMemoryCircuitBreakerRegistry(circuitBreakerConfig);
    }
}
