package com.lulobank.credits.starter.v3.adapters.out.sqs.reporting.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.starter.v3.adapters.out.sqs.reporting.SqsNotificationReportingAdapter;
import com.lulobank.credits.v3.port.out.queue.ReportingQueueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class SqsNotificationReportingConfig {

    @Value("${cloud.aws.sqs.queue.reporting-events}")
    private String reportingSqsEndpoint;


    @Bean
    public ReportingQueueService reportingQueueService(SqsBraveTemplate sqsBraveTemplate) {
        return new SqsNotificationReportingAdapter(reportingSqsEndpoint, sqsBraveTemplate);
    }
}
