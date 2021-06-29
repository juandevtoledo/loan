package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.adapters.out.dynamo.CreditsRepositoryConfig;
import com.lulobank.credits.starter.v3.adapters.out.flexibility.FlexibilityAdapterConfig;
import com.lulobank.credits.starter.v3.adapters.out.schedule.config.ScheduleAdapterConfig;
import com.lulobank.credits.starter.v3.adapters.out.sqs.reporting.config.SqsNotificationReportingConfig;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.queue.ReportingQueueService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerNotificationAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.service.AutomaticDebitPaymentService;
import com.lulobank.credits.v3.service.CloseLoanService;
import com.lulobank.credits.v3.service.FailurePaymentService;
import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.usecase.automaticdebit.MakeAutomaticPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.CustomPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.MinimumPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.PaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.TotalPaymentUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CreditsRepositoryConfig.class, FlexibilityAdapterConfig.class, SqsNotificationReportingConfig.class, ScheduleAdapterConfig.class})
public class PaymentUseCaseConfig {

    @Bean
    public CloseLoanService closeLoanService(CreditsV3Repository creditsV3Repository, ReportingQueueService reportingQueueService, SchedulerTransactionAsyncService getSchedulerServiceSqsAdapter) {
        return new CloseLoanService(creditsV3Repository, reportingQueueService, getSchedulerServiceSqsAdapter);
    }


    @Bean
    public LoanPaymentService loanPaymentService(CoreBankingService coreBankingService, CreditsV3Repository creditsV3Repository) {
        return new LoanPaymentService(coreBankingService, creditsV3Repository);
    }

    @Bean
    public FailurePaymentService failurePaymentService(CoreBankingService coreBankingService , SchedulerTransactionAsyncService schedulerServiceSqsAdapter){
        return new FailurePaymentService (coreBankingService , schedulerServiceSqsAdapter);
    }

    @Bean
    public AutomaticDebitPaymentService automaticDebitPaymentService(LoanPaymentService loanPaymentService, CloseLoanService closeLoanService,
                                                                     SchedulerTransactionAsyncService schedulerServiceSqsAdapter , FailurePaymentService failurePaymentService) {
        return new AutomaticDebitPaymentService(loanPaymentService, closeLoanService, schedulerServiceSqsAdapter , failurePaymentService);
    }

    @Bean
    public MinimumPaymentUseCase paymentMinimumUseCase(LoanPaymentService loanPaymentService) {
        return new MinimumPaymentUseCase(loanPaymentService);
    }

    @Bean
    public CustomPaymentUseCase paymentCustomUseCase(LoanPaymentService loanPaymentService) {
        return new CustomPaymentUseCase(loanPaymentService);
    }

    @Bean
    public TotalPaymentUseCase totalPaymentUseCase(LoanPaymentService loanPaymentService, CloseLoanService closeLoanService) {
        return new TotalPaymentUseCase(loanPaymentService, closeLoanService);
    }

    @Bean
    public MakeAutomaticPaymentUseCase makeAutomaticPaymentUseCase(CreditsV3Repository creditsV3Repository, AutomaticDebitPaymentService automaticDebitPaymentService,
                                                                   CoreBankingService coreBankingService, SchedulerTransactionAsyncService schedulerAsyncService,
                                                                   SchedulerNotificationAsyncService schedulerNotificationAsyncService) {

        return new MakeAutomaticPaymentUseCase(creditsV3Repository, automaticDebitPaymentService, coreBankingService, schedulerAsyncService, schedulerNotificationAsyncService);

    }

    @Bean
    public PaymentUseCase paymentUseCase(LoanPaymentService loanPaymentService, CoreBankingService coreBankingService,CloseLoanService closeLoanService,CreditsV3Repository creditsV3Repository) {
        return new PaymentUseCase(loanPaymentService, coreBankingService, closeLoanService, creditsV3Repository);
    }
}
