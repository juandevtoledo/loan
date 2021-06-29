package com.lulobank.credits.starter.v3.adapters.out.clients;


import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.port.out.ClientsAsyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientsAdapterConfig {

    @Value("${cloud.aws.sqs.queue.client-events}")
    private String sqsEndpointClients;

    @Bean
    public ClientsAsyncService clientsAsyncService(SqsBraveTemplate sqsBraveTemplate) {
        return new ClientsSqsAdapter(sqsBraveTemplate, sqsEndpointClients);
    }
}
