package com.lulobank.credits.services.outboundadapters.sqs;

import com.lulobank.core.Command;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.events.Event;
import com.lulobank.credits.services.features.services.SQSMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageDeliveryException;

import static com.lulobank.credits.services.utils.LogMessages.SQS_ERROR;

public abstract class SendMessageToClientSQS<T extends Command> implements Action<Response, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageToClientSQS.class);
    private final SQSMessageService sqsMessageService;

    @Autowired
    public SendMessageToClientSQS(SQSMessageService sqsMessageService) {
        this.sqsMessageService = sqsMessageService;
    }

    public abstract Event buildEvent(Response response, T command);

    @Override
    public void run(Response response, T command) {
        try {
            Event event = this.buildEvent(response, command);
            sqsMessageService.sendMessageClientQueue(event);
            LOGGER.info("Send event : {} , {}  to sqs {}", event.getId(), event.getEventType(), sqsMessageService.getSqsEndpoint());
        } catch (MessageDeliveryException ex) {
            LOGGER.error(SQS_ERROR.getMessage(), ex.getMessage(), ex);
        }
    }
}
