package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.adapters.out.dynamo.CreditsRepositoryConfig;
import com.lulobank.credits.starter.v3.adapters.out.sqs.pse.config.SqsPseNotificationAdapterConfig;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.PseAsyncService;
import com.lulobank.credits.v3.service.CloseLoanService;
import com.lulobank.credits.v3.usecase.closeloan.CloseLoanByExternalPaymentUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CreditsRepositoryConfig.class, SqsPseNotificationAdapterConfig.class})
public class CloseLoanByExternalPaymentUseCaseConfig {

    @Bean
    public CloseLoanByExternalPaymentUseCase closeLoanByExternalPaymentUseCase(CreditsV3Repository creditsV3Repository,
                                                                               CloseLoanService closeLoanService, PseAsyncService pseAsyncService) {
        return new CloseLoanByExternalPaymentUseCase(creditsV3Repository, closeLoanService, pseAsyncService);
    }
}
