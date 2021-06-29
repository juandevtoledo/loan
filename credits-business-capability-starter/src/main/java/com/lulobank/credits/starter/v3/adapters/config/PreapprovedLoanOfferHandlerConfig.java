package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.handler.PreapprovedLoanOfferHandler;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.PreapprovedLoanOffersUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PreapprovedLoanOfferHandlerConfig {

    @Bean
    public PreapprovedLoanOfferHandler preapprovedLoanOfferHandler(PreapprovedLoanOffersUseCase preapprovedLoanOffersUseCase) {
        return new PreapprovedLoanOfferHandler(preapprovedLoanOffersUseCase);
    }

}
