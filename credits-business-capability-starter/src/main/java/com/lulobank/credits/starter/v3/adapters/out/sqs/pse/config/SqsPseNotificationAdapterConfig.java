package com.lulobank.credits.starter.v3.adapters.out.sqs.pse.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.starter.v3.adapters.out.sqs.pse.SqsPseNotificationAdapter;
import com.lulobank.credits.v3.port.out.queue.PseAsyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqsPseNotificationAdapterConfig {

    @Value("${cloud.aws.sqs.queue.pse-events}")
    private String pseSqsEndpoint;

    @Bean
    public PseAsyncService pseAsyncService(SqsBraveTemplate sqsBraveTemplate) {
        return new SqsPseNotificationAdapter(pseSqsEndpoint, sqsBraveTemplate);
    }
}
