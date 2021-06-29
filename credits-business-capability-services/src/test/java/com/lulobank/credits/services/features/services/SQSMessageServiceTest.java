package com.lulobank.credits.services.features.services;

import com.lulobank.core.events.Event;
import com.lulobank.credits.services.events.CBSCreated;
import com.lulobank.credits.services.events.SavingsAccountCreated;
import com.lulobank.credits.services.events.YaTransferCreateWallet;
import com.lulobank.credits.services.features.services.model.SQSEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SQSMessageServiceTest {

    @Mock
    QueueMessagingTemplate queueMessagingTemplate;

    private SQSMessageService testedClass;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        SQSEndpoint sqsEndpoint = new SQSEndpoint();
        sqsEndpoint.setSqsEndPointClient("");
        sqsEndpoint.setSqsEndPointTransaction("");
        sqsEndpoint.setSqsEndPointSavingAccount("");
        sqsEndpoint.setSqsEndpointReporting("");
        this.testedClass = new SQSMessageService(sqsEndpoint,queueMessagingTemplate);
    }

    @Test
    public void Validate_Send_SQSMessage_To_SavingAccount_Queue(){
        Event<SavingsAccountCreated> event = new Event<>();
        testedClass.sendMessageSavingAccountQueue(event);
        verify(queueMessagingTemplate, times(1)).convertAndSend(anyString(), any(Event.class));
    }

    @Test
    public void Validate_Send_SQSMessage_To_Client_Queue(){
        Event<CBSCreated> event = new Event<>();
        testedClass.sendMessageClientQueue(event);
        verify(queueMessagingTemplate, times(1)).convertAndSend(anyString(), any(Event.class));
    }

    @Test
    public void Validate_Send_SQSMessage_To_Transaction_Queue(){
        Event<YaTransferCreateWallet> event = new Event<>();
        testedClass.sendMessageTransactionQueue(event);
        verify(queueMessagingTemplate, times(1)).convertAndSend(anyString(), any(Event.class));
    }

    @Test
    public void Validate_Send_SQSMessage_To_Reporting_Queue(){
        Event<YaTransferCreateWallet> event = new Event<>();
        testedClass.sendMessageReportingQueue(event, new HashMap<>());
        verify(queueMessagingTemplate, times(1)).convertAndSend(anyString(), any(Event.class), anyMap());
    }
}
