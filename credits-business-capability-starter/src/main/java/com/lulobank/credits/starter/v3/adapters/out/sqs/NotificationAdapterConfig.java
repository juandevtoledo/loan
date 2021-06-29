package com.lulobank.credits.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.port.out.queue.NotificationV3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationAdapterConfig {

    @Value("${cloud.aws.sqs.queue.client-events}")
    private String clientSqsEndpoint;

    @Value("${cloud.aws.sqs.queue.reporting-events}")
    private String reportingSqsEndpoint;

    @Value("${cloud.aws.sqs.queue.reporting-events-v2}")
    private String reportingSqsEndpointV2;

    @Bean
    public NotificationV3Service notificationV3Service(SqsBraveTemplate sqsTemplate) {
        ClientCBSCreatedEvent clientCBSCreatedEvent = new ClientCBSCreatedEvent(clientSqsEndpoint);
        PersistLoanDocumentEvent persistLoanDocumentEvent = new PersistLoanDocumentEvent(reportingSqsEndpoint);
        ApprovedLoanDocumentEvent approvedLoanDocumentEvent = new ApprovedLoanDocumentEvent(reportingSqsEndpointV2);
        SendSignedDocumentGroupSqs sendSignedDocumentGroupSqs = new SendSignedDocumentGroupSqs(reportingSqsEndpoint);
        RiskEngineResultEventV2Event riskEngineResultEventV2Event = new RiskEngineResultEventV2Event(clientSqsEndpoint);

        return new QueueServiceAdapter(clientCBSCreatedEvent, persistLoanDocumentEvent, sqsTemplate,
                approvedLoanDocumentEvent, sendSignedDocumentGroupSqs, riskEngineResultEventV2Event);
    }

}
