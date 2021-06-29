package com.lulobank.credits.services.features.services;

import com.lulobank.core.events.Event;
import com.lulobank.credits.services.features.services.model.SQSEndpoint;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.Map;

//TODO separar la dos responsabilidades
public class SQSMessageService {

    private SQSEndpoint sqsEndpoint;
    private QueueMessagingTemplate queueMessagingTemplate;

    public SQSMessageService(SQSEndpoint sqsEndpoint , QueueMessagingTemplate queueMessagingTemplate) {
        this.sqsEndpoint = sqsEndpoint;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void sendMessageClientQueue(Event event){
        queueMessagingTemplate.convertAndSend(sqsEndpoint.getSqsEndPointClient(), event);
    }

    public void sendMessageSavingAccountQueue(Event event){
        queueMessagingTemplate.convertAndSend(sqsEndpoint.getSqsEndPointSavingAccount(), event);
    }

    public void sendMessageTransactionQueue(Event event){
        queueMessagingTemplate.convertAndSend(sqsEndpoint.getSqsEndPointTransaction(), event);
    }

    public void sendMessageReportingQueue(Event event, Map<String, Object> headers) {
        queueMessagingTemplate.convertAndSend(sqsEndpoint.getSqsEndpointReporting(), event, headers);
    }

    public SQSEndpoint getSqsEndpoint() {
        return sqsEndpoint;
    }

}