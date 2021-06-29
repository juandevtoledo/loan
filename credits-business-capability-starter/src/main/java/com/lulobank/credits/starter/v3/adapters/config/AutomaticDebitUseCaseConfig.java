package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.adapters.out.dynamo.CreditsRepositoryConfig;
import com.lulobank.credits.starter.v3.adapters.out.schedule.config.ScheduleAdapterConfig;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.usecase.automaticdebitoption.AutomaticDebitOptionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CreditsRepositoryConfig.class, ScheduleAdapterConfig.class})
public class AutomaticDebitUseCaseConfig {

    @Bean
    public AutomaticDebitOptionUseCase automaticDebitOptionUseCase(CreditsV3Repository creditsV3Repository,
                SchedulerTransactionAsyncService schedulerTransactionAsyncService) {
        return new AutomaticDebitOptionUseCase(creditsV3Repository, schedulerTransactionAsyncService);
    }
}
