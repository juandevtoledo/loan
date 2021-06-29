package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.handler.LoanPaymentPlanHandler;
import com.lulobank.credits.starter.v3.handler.PaymentPlanHandler;
import com.lulobank.credits.v3.usecase.paymentplan.LoanPaymentPlantUseCase;
import com.lulobank.credits.v3.usecase.paymentplan.PaymentPlantUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentPlanHandlerConfig {

    @Bean
    public PaymentPlanHandler paymentPlanHandler(PaymentPlantUseCase paymentPlantUseCase) {
        return new PaymentPlanHandler(paymentPlantUseCase);
    }

    @Bean
    public LoanPaymentPlanHandler loanPaymentPlanHandler(LoanPaymentPlantUseCase loanPaymentPlantUseCase) {
        return new LoanPaymentPlanHandler(loanPaymentPlantUseCase);
    }
}
