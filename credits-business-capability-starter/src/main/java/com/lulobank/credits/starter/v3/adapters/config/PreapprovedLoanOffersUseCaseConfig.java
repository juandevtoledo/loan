package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.adapters.out.dynamo.CreditsRepositoryConfig;
import com.lulobank.credits.starter.v3.adapters.out.flexibility.FlexibilityAdapterConfig;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.service.SimulateByRiskResponse;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.PreapprovedLoanOffersUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CreditsRepositoryConfig.class, CreditsConfig.class, FlexibilityAdapterConfig.class})
public class PreapprovedLoanOffersUseCaseConfig {

    @Bean
    public PreapprovedLoanOffersUseCase preapprovedLoanOffersUseCase(CreditsV3Repository creditsV3Repository, SimulateByRiskResponse simulateByRiskResponse, CoreBankingService coreBankingService) {
        return new PreapprovedLoanOffersUseCase(creditsV3Repository, coreBankingService, simulateByRiskResponse);
    }

}
