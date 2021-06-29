package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.adapters.out.dynamo.CreditsRepositoryConfig;
import com.lulobank.credits.starter.v3.adapters.out.flexibility.FlexibilityAdapterConfig;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.usecase.loandetail.LoanDetailUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CreditsRepositoryConfig.class, FlexibilityAdapterConfig.class})
public class LoanDetailUseCaseConfig {

    @Bean
    public LoanDetailUseCase loanDetailUseCase(CreditsV3Repository creditsV3Repository, CoreBankingService coreBankingService) {
        return new LoanDetailUseCase(creditsV3Repository, coreBankingService);
    }
}
