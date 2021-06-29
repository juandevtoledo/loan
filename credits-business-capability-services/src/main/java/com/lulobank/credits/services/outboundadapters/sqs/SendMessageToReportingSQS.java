package com.lulobank.credits.services.outboundadapters.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.core.Command;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.events.Event;
import com.lulobank.credits.services.features.services.SQSMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageDeliveryException;

import java.util.Objects;

import static com.lulobank.credits.services.utils.LogMessages.SQS_ERROR;
import static com.lulobank.credits.services.utils.SQSUtil.getMessageHeaders;

public abstract  class SendMessageToReportingSQS <T extends Command> implements Action<Response, T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageToReportingSQS.class);
    private final SQSMessageService sqsMessageService;

    @Autowired
    public SendMessageToReportingSQS(SQSMessageService sqsMessageService) {
        this.sqsMessageService = sqsMessageService;
    }

    public abstract Event buildEvent(Response response, T command);

    @Override
    public void run(Response response, T command) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Event event = this.buildEvent(response, command);
            if (Objects.nonNull(event)) {
                sqsMessageService.sendMessageReportingQueue(event, getMessageHeaders(command));
                String jsonEvent = objectMapper.writeValueAsString(event);
                LOGGER.info("Send event sqs reporting: {} , {}  to sqs {}, message json {}", event.getId(), event.getEventType(), sqsMessageService.getSqsEndpoint(), jsonEvent);
            }
        } catch (MessageDeliveryException | JsonProcessingException ex) {
            LOGGER.error(SQS_ERROR.getMessage(), ex.getMessage(), ex);
        }
    }
}
