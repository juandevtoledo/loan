package com.lulobank.credits.v3.port.out.scheduler.automaticdebit;

import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;

public interface SchedulerNotificationAsyncService {

    void successNotification(TransactionRequest transactionRequest);

    void failedNotification(TransactionRequest transactionRequest);
}
