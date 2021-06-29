package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.adapters.out.dynamo.CreditsRepositoryConfig;
import com.lulobank.credits.starter.v3.adapters.out.flexibility.FlexibilityAdapterConfig;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.usecase.movement.LoanMovementsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CreditsRepositoryConfig.class, FlexibilityAdapterConfig.class})
public class LoanMovementsUseCaseConfig {

    @Bean
    public LoanMovementsUseCase loanMovementsUseCase(CreditsV3Repository creditsV3Repository, CoreBankingService coreBankingService) {
        return new LoanMovementsUseCase(creditsV3Repository, coreBankingService);
    }
}
