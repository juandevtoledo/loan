package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.handler.CustomPaymentHandler;
import com.lulobank.credits.starter.v3.handler.MinPaymentHandler;
import com.lulobank.credits.starter.v3.handler.PaymentHandler;
import com.lulobank.credits.starter.v3.handler.TotalPaymentHandler;
import com.lulobank.credits.v3.usecase.payment.CustomPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.MinimumPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.PaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.TotalPaymentUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentHandlerConfig {

    @Bean
    public MinPaymentHandler paymentMinimumHandler(MinimumPaymentUseCase minimumPaymentUseCase) {
        return new MinPaymentHandler(minimumPaymentUseCase);
    }

    @Bean
    public CustomPaymentHandler customPaymentHandler(CustomPaymentUseCase customPaymentUseCase) {
        return new CustomPaymentHandler(customPaymentUseCase);
    }

    @Bean
    public TotalPaymentHandler totalPaymentHandler(TotalPaymentUseCase totalPaymentUseCase) {
        return new TotalPaymentHandler(totalPaymentUseCase);
    }

    @Bean
    public PaymentHandler paymentHandler(PaymentUseCase paymentUseCase) {
        return new PaymentHandler(paymentUseCase);
    }
}
