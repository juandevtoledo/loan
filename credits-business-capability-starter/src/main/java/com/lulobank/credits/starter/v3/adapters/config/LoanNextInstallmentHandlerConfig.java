package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.handler.LoanNextInstallmentHandler;
import com.lulobank.credits.v3.port.in.nextinstallment.GenerateNextInstallmentPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanNextInstallmentHandlerConfig {

    @Bean
    public LoanNextInstallmentHandler loanNextInstallmentHandler(GenerateNextInstallmentPort generateNextInstallmentPort) {
        return new LoanNextInstallmentHandler(generateNextInstallmentPort);
    }
}
