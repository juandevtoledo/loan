package com.lulobank.credits.starter.v3.checker.config;

import com.lulobank.credits.starter.v3.checker.FlexibilitySdkConfigChecker;
import flexibility.client.sdk.FlexibilitySdk;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckerConfig {

    @ConditionalOnProperty(
            value="health.flexibility.enabled",
            havingValue = "true",
            matchIfMissing = false)
    @Bean("FlexibilityConnection")
	public FlexibilitySdkConfigChecker getFlexibilitySdkConfigChecker(FlexibilitySdk flexibilitySdk) {
		return new FlexibilitySdkConfigChecker(flexibilitySdk);
	}
}
