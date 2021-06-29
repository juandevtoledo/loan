package com.lulobank.credits.starter.v3.adapters.out.clientalerts;


import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.port.out.ClientAlertsAsyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientAlertsAdapterConfig {

    @Value("${cloud.aws.sqs.queue.client-alerts-events}")
    private String sqsEndpointClientAlerts;

    @Bean
    public ClientAlertsAsyncService clientAlertsAsyncService(SqsBraveTemplate sqsBraveTemplate) {
        return new ClientAlertsSqsAdapter(sqsBraveTemplate, sqsEndpointClientAlerts);
    }
}
