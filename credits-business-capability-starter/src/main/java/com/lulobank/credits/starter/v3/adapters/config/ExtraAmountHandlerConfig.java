package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.handler.ExtraAmountHandler;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.usecase.installment.ExtraAmountInstallmentUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExtraAmountHandlerConfig {

    @Bean
    public ExtraAmountHandler getExtraAmountHandler(ExtraAmountInstallmentUseCase extraAmountInstallmentUseCase){
        return new ExtraAmountHandler(extraAmountInstallmentUseCase);
    }
    @Bean
    public ExtraAmountInstallmentUseCase getExtraAmountInstallmentUseCase(CreditsV3Repository creditsV3Repository,
                                                                          CoreBankingService coreBankingService){
        return new ExtraAmountInstallmentUseCase(coreBankingService,creditsV3Repository);
    }
}
