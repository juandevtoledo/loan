package com.lulobank.credits.starter.v3.adapters.in.sqs;

import com.lulobank.credits.starter.v3.adapters.config.PreApprovedClientOfferServiceConfig;
import com.lulobank.credits.starter.v3.adapters.config.RiskEngineResultValidationsServiceConfig;
import com.lulobank.credits.starter.v3.adapters.out.schedule.config.ScheduleAdapterConfig;
import com.lulobank.credits.starter.v3.adapters.out.sqs.reportingxbc.config.SqsNotificationLoanStatementConfig;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResponseUseCase;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResultEventV2UseCase;
import com.lulobank.credits.v3.port.in.approvedriskengine.LoanAssessmentResultUseCase;
import com.lulobank.credits.v3.port.in.clientinformation.UpdateProductEmailUseCase;
import com.lulobank.credits.v3.port.in.digitalevidence.DigitalEvidenceCreatedUseCase;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.promissorynote.PromissoryNoteCreatedUseCase;
import com.lulobank.credits.v3.port.in.rescheduledloan.RescheduledLoanUseCase;
import com.lulobank.credits.v3.port.out.ClientAlertsAsyncService;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferNotificationService;
import com.lulobank.credits.v3.port.out.queue.NotificationLoanStatement;
import com.lulobank.credits.v3.port.out.queue.NotificationV3Service;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.service.PreApprovedClientOfferService;
import com.lulobank.credits.v3.service.RiskEngineResultValidationsService;
import com.lulobank.credits.v3.usecase.IntentionLoanStatementUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({SqsNotificationLoanStatementConfig.class, ScheduleAdapterConfig.class, RiskEngineResultValidationsServiceConfig.class,
	PreApprovedClientOfferServiceConfig.class})
public class UseCaseConfig {

    @Bean
    public UpdateProductEmailUseCase getUpdateProductEmailUseCase(CreditsV3Repository creditsRepository) {
        return new UpdateProductEmailUseCase(creditsRepository);
    }

    @Bean
    public DigitalEvidenceCreatedUseCase getDigitalEvidenceCreatedUseCase(CreditsV3Repository creditsRepository,
                                                                          LoanV3Service loanV3Service,
                                                                          NotificationV3Service notificationV3Service,
                                                                          ClientAlertsAsyncService clientAlertsAsyncService,
                                                                          SchedulerTransactionAsyncService schedulerAutomaticDebitAsyncService) {
        return new DigitalEvidenceCreatedUseCase(creditsRepository, loanV3Service,
                notificationV3Service, clientAlertsAsyncService, schedulerAutomaticDebitAsyncService);
    }

    @Bean
    public PromissoryNoteCreatedUseCase getPromissoryNoteCreatedUseCase(CreditsV3Repository creditsRepository, NotificationV3Service notificationV3Service) {
        return new PromissoryNoteCreatedUseCase(creditsRepository, notificationV3Service);
    }

    @Bean
    public IntentionLoanStatementUseCase getIntentionLoanStatementUseCase(CreditsV3Repository creditsRepository, NotificationLoanStatement notificationLoanStatement, 
    		CoreBankingService coreBankingService) {
        return new IntentionLoanStatementUseCase(creditsRepository, notificationLoanStatement, coreBankingService);
    }
    
    @Bean
    public RiskEngineResponseUseCase getRiskEngineResponseUseCase(CreditsV3Repository creditsRepository) {
    	return new RiskEngineResponseUseCase(creditsRepository);
    }

    @Bean
    public RiskEngineResultEventV2UseCase getRiskEngineResultEventV2UseCase(CreditsV3Repository creditsRepository, 
    		ProductOfferNotificationService productOfferNotificationService, RiskEngineResultValidationsService riskEngineResultValidationsService,
    		PreApprovedClientOfferService preApprovedClientOfferService) {
        return new RiskEngineResultEventV2UseCase(creditsRepository, productOfferNotificationService, riskEngineResultValidationsService, preApprovedClientOfferService);
    }
    
    @Bean
    public LoanAssessmentResultUseCase getLoanAssessmentResultUseCase(CreditsV3Repository creditsRepository, 
    		ProductOfferNotificationService productOfferNotificationService, RiskEngineResultValidationsService riskEngineResultValidationsService,
    		PreApprovedClientOfferService preApprovedClientOfferService) {
        return new LoanAssessmentResultUseCase(creditsRepository, productOfferNotificationService, riskEngineResultValidationsService, preApprovedClientOfferService);
    }

    @Bean
    public RescheduledLoanUseCase getRescheduledLoanUseCase(CreditsV3Repository creditsRepository) {
        return new RescheduledLoanUseCase(creditsRepository);
    }
}
