package com.lulobank.credits.starter.v3.adapters.out.schedule.config;

import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerNotificationAsyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.credits.starter.v3.adapters.out.schedule.SchedulerTransactionSqsAdapter;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;

@Configuration
public class ScheduleAdapterConfig {
	
	@Value("${cloud.aws.sqs.queue.scheduler-events}")
	private String schedulerSqsEndpoint;
	
    @Value("${cloud.aws.sqs.listeners.application-events}")
    private String creditsSqsEndpoint;
    
    @Bean
    public SchedulerTransactionAsyncService getSchedulerServiceSqsAdapter(SqsBraveTemplate sqsBraveTemplate) {
    	return new SchedulerTransactionSqsAdapter(sqsBraveTemplate, schedulerSqsEndpoint, creditsSqsEndpoint);
    }

    @Bean
    public SchedulerNotificationAsyncService schedulerNotificationAsyncService(SqsBraveTemplate sqsBraveTemplate) {
        return new SchedulerTransactionSqsAdapter(sqsBraveTemplate, schedulerSqsEndpoint, creditsSqsEndpoint);
    }
}
