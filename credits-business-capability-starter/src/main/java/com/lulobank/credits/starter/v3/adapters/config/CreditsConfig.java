package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.adapters.out.clients.ClientsAdapterConfig;
import com.lulobank.credits.starter.v3.adapters.out.dynamo.CreditsRepositoryConfig;
import com.lulobank.credits.starter.v3.adapters.out.flexibility.FlexibilityAdapterConfig;
import com.lulobank.credits.starter.v3.adapters.out.loan.LoanConfig;
import com.lulobank.credits.starter.v3.adapters.out.otp.ValidateSignAdapterConfig;
import com.lulobank.credits.starter.v3.adapters.out.promissorynote.PromissoryNoteAdapterConfig;
import com.lulobank.credits.starter.v3.adapters.out.savingsaccounts.SavingsAccountsAdapterConfig;
import com.lulobank.credits.starter.v3.adapters.out.sqs.NotificationAdapterConfig;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.promissorynote.ValidForPromissoryNoteSing;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.promissorynote.PromissoryNoteAsyncService;
import com.lulobank.credits.v3.port.out.saving.SavingAccountService;
import com.lulobank.credits.v3.service.CalculateFlexibleInstallmentService;
import com.lulobank.credits.v3.service.CreateOffersService;
import com.lulobank.credits.v3.service.OfferService;
import com.lulobank.credits.v3.service.SimulateByFormulaService;
import com.lulobank.credits.v3.service.SimulateByRiskResponse;
import com.lulobank.credits.v3.service.SimulateService;
import com.lulobank.credits.v3.usecase.AcceptOfferV3UseCase;
import com.lulobank.credits.v3.usecase.PaymentPlantV3UseCase;
import com.lulobank.credits.v3.usecase.intialsoffersv3.InitialsOffersV3UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ValidateSignAdapterConfig.class,
        NotificationAdapterConfig.class,
        PromissoryNoteAdapterConfig.class,
        SavingsAccountsAdapterConfig.class,
        FlexibilityAdapterConfig.class,
        LoanConfig.class,
        CreditsRepositoryConfig.class,
        CreditsConditionV3Config.class,
        ClientsAdapterConfig.class
})
public class CreditsConfig {

    @Bean
    public AcceptOfferV3UseCase acceptOfferHandlerV3(ValidForPromissoryNoteSing validForPromissoryNoteSing, 
    																		PromissoryNoteAsyncService promissoryNoteAsyncService,
                                                                            CreditsV3Repository creditsV3Repository,
                                                                            OfferService offerService,
                                                                            CreditsConditionV3 creditsConditionV3,
                                                                            LoanV3Service loanV3Service,
                                                                            SavingAccountService savingAccountService) {
        return new AcceptOfferV3UseCase(validForPromissoryNoteSing, promissoryNoteAsyncService,
                creditsV3Repository, offerService, creditsConditionV3, loanV3Service, savingAccountService);
    }

    @Bean
    public PaymentPlantV3UseCase paymentPlantV3UseCase(CreditsV3Repository creditsV3Repository,
                                                       LoanV3Service loanV3Service) {
        return new PaymentPlantV3UseCase(creditsV3Repository, loanV3Service);
    }

    @Bean
    public OfferService offerService() {
        return new OfferService();
    }

    @Bean
    public SimulateService simulateService(CoreBankingService coreBankingService) {
        return new SimulateService(coreBankingService);
    }

    @Bean
    public SimulateByFormulaService simulateByFormulaService(CalculateFlexibleInstallmentService calculateFlexibleInstallmentService) {
        return new SimulateByFormulaService(calculateFlexibleInstallmentService);
    }

    @Bean
    public SimulateByRiskResponse simulateByRiskResponse(CalculateFlexibleInstallmentService calculateFlexibleInstallmentService) {
        return new SimulateByRiskResponse(calculateFlexibleInstallmentService);
    }

    @Bean
    public CalculateFlexibleInstallmentService calculateFlexibleInstallmentService() {
        return new CalculateFlexibleInstallmentService();
    }

    @Bean
    public CreateOffersService createOffersService(SimulateByFormulaService simulateByFormulaService, SimulateService simulateService) {
        return new CreateOffersService(simulateByFormulaService, simulateService);
    }

    @Bean
    public InitialsOffersV3UseCase initialsOffersV3UseCase(CreditsV3Repository creditsV3Repository, CreateOffersService createOffersService,CreditsConditionV3Config creditsConditionV3Config) {
        return new InitialsOffersV3UseCase(creditsV3Repository, createOffersService,creditsConditionV3Config.creditsConditionV3().getFeeInsurance());
    }

}
