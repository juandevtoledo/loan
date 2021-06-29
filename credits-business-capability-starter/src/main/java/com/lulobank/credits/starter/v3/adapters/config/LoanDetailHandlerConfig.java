package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.handler.LoanDetailHandler;
import com.lulobank.credits.v3.usecase.loandetail.LoanDetailUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanDetailHandlerConfig {

    @Bean
    public LoanDetailHandler loanInformationHandler(LoanDetailUseCase loanDetailUseCase){
        return new LoanDetailHandler(loanDetailUseCase);
    }
}
