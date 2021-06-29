package com.lulobank.credits.services.actions;


import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.events.Event;
import com.lulobank.credits.services.events.CBSCreated;
import com.lulobank.credits.services.features.services.SQSMessageService;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanClientResponse;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanForClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SendMessageToSQSClients implements Action<Response<CreateLoanClientResponse>, CreateLoanForClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageToSQSClients.class);

    private SQSMessageService sqsMessageService;

    public SendMessageToSQSClients(SQSMessageService sqsMessageService){
        this.sqsMessageService=sqsMessageService;
    }

    @Override
    public void run(Response<CreateLoanClientResponse> loanClientResponseResponse, CreateLoanForClient loanClientRequest) {
        CBSCreated cbsCreated = new CBSCreated();
        cbsCreated.setIdCbs(loanClientResponseResponse.getContent().getIdClient());
        cbsCreated.setIdCbsHash(loanClientResponseResponse.getContent().getIdClientHash());
        cbsCreated.setIdClient(loanClientRequest.getIdClient());
        Event<CBSCreated> event = new Event<>();
        event.setEventType(CBSCreated.class.getSimpleName());
        event.setPayload(cbsCreated);
        event.setId(UUID.randomUUID().toString());

        sqsMessageService.sendMessageClientQueue(event);

        LOGGER.info("Send event : {} , {}  to sqs {}",event.getId(),event.getEventType(),sqsMessageService.getSqsEndpoint().getSqsEndPointClient());
    }
}
