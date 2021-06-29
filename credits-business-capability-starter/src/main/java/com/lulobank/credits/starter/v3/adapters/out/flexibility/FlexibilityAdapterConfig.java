package com.lulobank.credits.starter.v3.adapters.out.flexibility;

import com.lulobank.credits.starter.v3.adapters.config.CreditsConditionV3Config;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.tracing.FunctionBrave;
import flexibility.client.sdk.FlexibilitySdk;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CreditsConditionV3Config.class,})
public class FlexibilityAdapterConfig {

    @Bean
    public CoreBankingService coreBankingService(FlexibilitySdk flexibilitySdk, FunctionBrave functionBrave,
                                                 CreditsConditionV3 creditsConditionV3,
                                                 CircuitBreakerRegistry circuitBreakerRegistry) {
        return new FlexibilityAdapter(flexibilitySdk, functionBrave, creditsConditionV3,
                circuitBreakerRegistry.circuitBreaker("flexibility"));
    }

}
