package com.lulobank.credits.starter.v3.adapters.out.sqs.reportingxbc.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.starter.v3.adapters.out.sqs.reportingxbc.SqsNotificationLoanStatementAdapter;
import com.lulobank.credits.v3.port.out.queue.NotificationLoanStatement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqsNotificationLoanStatementConfig {

    @Value("${cloud.aws.sqs.queue.reporting-rx-events.url}")
    private String reportingSqsEndpoint;

    @Value("${cloud.aws.sqs.queue.reporting-rx-events.max-number-of-messages}")
    private Integer maximumReceives;

    @Value("${cloud.aws.sqs.queue.reporting-rx-events.delay}")
    private Integer delay;

    @Bean
    public NotificationLoanStatement notificationLoanStatement(SqsBraveTemplate sqsBraveTemplate) {
        return new SqsNotificationLoanStatementAdapter(reportingSqsEndpoint,sqsBraveTemplate, maximumReceives, delay);
    }
}