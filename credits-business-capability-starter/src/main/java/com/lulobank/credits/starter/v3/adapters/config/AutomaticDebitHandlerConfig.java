package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.handler.AutomaticDebitOptionHandler;
import com.lulobank.credits.v3.usecase.automaticdebitoption.AutomaticDebitOptionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutomaticDebitHandlerConfig {

    @Bean
    public AutomaticDebitOptionHandler automaticDebitOptionHandler(
            AutomaticDebitOptionUseCase automaticDebitOptionUseCase) {
        return new AutomaticDebitOptionHandler(automaticDebitOptionUseCase);
    }
}
