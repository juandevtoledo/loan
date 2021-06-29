package com.lulobank.credits.starter.v3.adapters.out.schedule;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.starter.v3.adapters.out.schedule.dto.CreateTransactionMessage;
import com.lulobank.credits.starter.v3.adapters.out.schedule.dto.CreateTransactionMessage.Payload;
import com.lulobank.credits.starter.v3.adapters.out.schedule.dto.CreateTransactionMessage.ReplyTo;
import com.lulobank.credits.starter.v3.adapters.out.schedule.dto.CreateTransactionMessage.TransactionEvent;
import com.lulobank.credits.starter.v3.adapters.out.schedule.dto.SaveTransactionResultMessage;
import com.lulobank.credits.starter.v3.adapters.out.schedule.dto.ScheduleType;
import com.lulobank.credits.starter.v3.adapters.out.schedule.dto.TransactionMessage;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerNotificationAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.CreateTransactionRequest;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;
import com.lulobank.events.api.EventFactory;
import lombok.AllArgsConstructor;

import static com.lulobank.credits.starter.v3.adapters.out.schedule.dto.ScheduleType.ONE_TIME;
import static com.lulobank.credits.starter.v3.adapters.out.schedule.dto.ScheduleType.SUBSCRIPTION;

@AllArgsConstructor
public class SchedulerTransactionSqsAdapter implements SchedulerTransactionAsyncService, SchedulerNotificationAsyncService {

    private static final String AUTOMATIC_DEBIT_EVENT_TYPE = "AutomaticDebitMessage";
    private static final String DELETE_TRANSACTION_EVENT_TYPE = "DeleteTransactionMessage";

    private final SqsBraveTemplate sqsBraveTemplate;
    private final String schedulerQueue;
    private final String creditsQueue;
    private static final int DELAY = 300;

    @Override
    public void createTransaction(CreateTransactionRequest createTransactionRequest) {
        sqsBraveTemplate.convertAndSend(schedulerQueue, EventFactory
                .ofDefaults(buildCreateTransactionMessage(createTransactionRequest, SUBSCRIPTION))
                .delay(DELAY)
                .build());
    }

    @Override
    public void deleteTransaction(TransactionRequest transactionRequest) {
        sqsBraveTemplate.convertAndSend(schedulerQueue, EventFactory
                .ofDefaults(buildDeleteTransactionMessage(transactionRequest))
                .eventType(DELETE_TRANSACTION_EVENT_TYPE)
                .delay(DELAY)
                .build());
    }

    @Override
    public void successNotification(TransactionRequest transactionRequest) {
        sqsBraveTemplate.convertAndSend(schedulerQueue, EventFactory
                .ofDefaults(saveTransactionResultMessage(transactionRequest, true))
                .delay(DELAY)
                .eventType("SaveTransactionResultMessage")
                .build());
    }

    @Override
    public void failedNotification(TransactionRequest transactionRequest) {
        sqsBraveTemplate.convertAndSend(schedulerQueue, EventFactory
                .ofDefaults(saveTransactionResultMessage(transactionRequest, false))
                .delay(DELAY)
                .eventType("SaveTransactionResultMessage")
                .build());
    }

    @Override
    public void oneTimeNotification(CreateTransactionRequest createTransactionRequest) {
        sqsBraveTemplate.convertAndSend(schedulerQueue, EventFactory
                .ofDefaults(buildCreateTransactionMessage(createTransactionRequest, ONE_TIME))
                .delay(DELAY)
                .build());
    }

    @Override
    public void retryTransaction(TransactionRequest transactionRequest) {
        sqsBraveTemplate.convertAndSend(schedulerQueue, EventFactory
                .ofDefaults(TransactionMessage.builder()
                        .idClient(transactionRequest.getIdClient())
                        .metadata(transactionRequest.getMetadata())
                        .build())
                .delay(DELAY)
                .eventType("RetryTransactionMessage")
                .build());
    }

    private CreateTransactionMessage buildCreateTransactionMessage(CreateTransactionRequest createTransactionRequest, ScheduleType scheduleType) {
        return CreateTransactionMessage.builder()
                .idClient(createTransactionRequest.getIdClient())
                .metadata(createTransactionRequest.getMetadata())
                .executionDay(createTransactionRequest.getDayOfPay())
                .type(scheduleType)
                .replyTo(ReplyTo.builder().sqs(creditsQueue).build())
                .transactionEvent(TransactionEvent.builder()
                        .eventType(AUTOMATIC_DEBIT_EVENT_TYPE)
                        .payload(Payload.builder()
                                .idCredit(createTransactionRequest.getIdCredit())
                                .build())
                        .build())
                .build();
    }

    private TransactionMessage buildDeleteTransactionMessage(TransactionRequest transactionRequest) {
        return TransactionMessage.builder()
                .idClient(transactionRequest.getIdClient())
                .metadata(transactionRequest.getMetadata())
                .build();
    }

    private SaveTransactionResultMessage saveTransactionResultMessage(TransactionRequest transactionRequest, boolean resulted) {
        return SaveTransactionResultMessage.builder()
                .idClient(transactionRequest.getIdClient())
                .metadata(transactionRequest.getMetadata())
                .result(SaveTransactionResultMessage.Result.builder()
                        .day(transactionRequest.getDayOfPay())
                        .success(resulted)
                        .build())
                .build();
    }

}
