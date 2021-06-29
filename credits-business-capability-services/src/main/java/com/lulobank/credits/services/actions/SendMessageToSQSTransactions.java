package com.lulobank.credits.services.actions;

import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import com.lulobank.credits.services.events.YaTransferCreateWallet;
import com.lulobank.credits.services.features.services.SQSMessageService;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanClientResponse;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanForClient;
import com.lulobank.credits.services.utils.ConverterObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendMessageToSQSTransactions implements Action<Response<CreateLoanClientResponse>, CreateLoanForClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageToSQSTransactions.class);
    private SQSMessageService sqsMessageService;

    public SendMessageToSQSTransactions(SQSMessageService sqsMessageService){
        this.sqsMessageService = sqsMessageService;
    }

    @Override
    public void run(Response<CreateLoanClientResponse> loanClientResponseResponse, CreateLoanForClient loanClientRequest) {
        YaTransferCreateWallet yaTransferCreateWallet = ConverterObjectUtil.createYaTransferCreateWallet(loanClientResponseResponse,loanClientRequest);
        Event<YaTransferCreateWallet> event = new EventUtils().getEvent(yaTransferCreateWallet);
        sqsMessageService.sendMessageTransactionQueue(event);
        LOGGER.info("Send event : {} , {}  to sqs {}",event.getId(),event.getEventType(),sqsMessageService.getSqsEndpoint().getSqsEndPointTransaction());
    }

}
