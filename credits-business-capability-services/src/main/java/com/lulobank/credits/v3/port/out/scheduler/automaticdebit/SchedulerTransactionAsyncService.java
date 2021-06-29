package com.lulobank.credits.v3.port.out.scheduler.automaticdebit;

import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.CreateTransactionRequest;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;

public interface SchedulerTransactionAsyncService {
	
	void createTransaction(CreateTransactionRequest createTransactionRequest);

	void deleteTransaction(TransactionRequest transactionRequest);

	void oneTimeNotification(CreateTransactionRequest createTransactionRequest);

	void retryTransaction(TransactionRequest transactionRequest);
}
