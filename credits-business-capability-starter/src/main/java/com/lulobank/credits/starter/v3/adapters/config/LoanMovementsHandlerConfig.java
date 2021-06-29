package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.handler.LoanMovementsHandler;
import com.lulobank.credits.v3.usecase.movement.LoanMovementsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanMovementsHandlerConfig {

    @Bean
    public LoanMovementsHandler loanMovementsHandler(LoanMovementsUseCase loanMovementsUseCase){
        return new LoanMovementsHandler(loanMovementsUseCase);
    }
}
