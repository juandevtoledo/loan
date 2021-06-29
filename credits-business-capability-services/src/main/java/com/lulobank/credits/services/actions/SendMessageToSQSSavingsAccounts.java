package com.lulobank.credits.services.actions;


import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.events.Event;
import com.lulobank.credits.services.events.SavingsAccountCreated;
import com.lulobank.credits.services.features.services.SQSMessageService;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanClientResponse;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanForClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

//TODO: cambiar nombre de clase
public class SendMessageToSQSSavingsAccounts implements Action<Response<CreateLoanClientResponse>, CreateLoanForClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageToSQSSavingsAccounts.class);

    private SQSMessageService sqsMessageService;

    public SendMessageToSQSSavingsAccounts(SQSMessageService sqsMessageService){
        this.sqsMessageService=sqsMessageService;
    }

    @Override
    public void run(Response<CreateLoanClientResponse> loanClientResponseResponse, CreateLoanForClient loanClientRequest) {
        SavingsAccountCreated cbsSavingAccount = new SavingsAccountCreated();
        cbsSavingAccount.setIdClient(loanClientRequest.getIdClient());
        cbsSavingAccount.setIdClientCBS(loanClientResponseResponse.getContent().getIdClient());
        cbsSavingAccount.setIdClientCBSHash(loanClientResponseResponse.getContent().getIdClientHash());
        cbsSavingAccount.setIdSavingAccount(loanClientResponseResponse.getContent().getSavingAccountNumber());
        cbsSavingAccount.setIdSavingAccountCBSHash(loanClientResponseResponse.getContent().getSavingAccountHash());
        Event<SavingsAccountCreated> event = new Event<>();
        event.setEventType(SavingsAccountCreated.class.getSimpleName());
        event.setPayload(cbsSavingAccount);
        event.setId(UUID.randomUUID().toString());

        sqsMessageService.sendMessageSavingAccountQueue(event);

        LOGGER.info("Send event : {} , {}  to sqs {}",event.getId(),event.getEventType(),sqsMessageService.getSqsEndpoint().getSqsEndPointSavingAccount());

    }
}
