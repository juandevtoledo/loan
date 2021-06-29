package com.lulobank.credits.starter.v3.adapters.out.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.lulobank.credits.starter.v3.adapters.out.schedule.dto.CreateTransactionMessage;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;
import com.lulobank.events.api.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.CreateTransactionRequest;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;

public class SchedulerServiceSqsAdapterTest {
	
	private SchedulerTransactionSqsAdapter schedulerServiceSqsAdapter;
	
	@Mock
	private SqsBraveTemplate sqsBraveTemplate;
	@Captor
	private ArgumentCaptor<Event<CreateTransactionMessage>> eventArgumentCaptor;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		schedulerServiceSqsAdapter = new SchedulerTransactionSqsAdapter(sqsBraveTemplate, "scheduleQueue", "creditsQueue");
	}
	
	@Test
	public void sendCreateMessageToScheduleService() {
		CreateTransactionRequest createTransactionRequest = buildCreateTransactionRequest();
		schedulerServiceSqsAdapter.createTransaction(createTransactionRequest);
		verify(sqsBraveTemplate, times(1)).convertAndSend(Mockito.anyString(), eventArgumentCaptor.capture());
		assertThat(eventArgumentCaptor.getValue().getPayload().getMetadata(),is("metadata"));
	}

	@Test
	public void sendDeleteMessageToScheduleService() {
		TransactionRequest transactionRequest = buildTransactionRequest();
		schedulerServiceSqsAdapter.deleteTransaction(transactionRequest);
		verify(sqsBraveTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any());
	}

	@Test
	public void sendRetryMessageToScheduleService() {
		TransactionRequest transactionRequest = buildTransactionRequest();
		schedulerServiceSqsAdapter.retryTransaction(transactionRequest);
		verify(sqsBraveTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any());
	}

	@Test
	public void sendSuccessNotificationToScheduleService() {
		TransactionRequest transactionRequest = buildTransactionRequest();
		schedulerServiceSqsAdapter.successNotification(transactionRequest);
		verify(sqsBraveTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any());
	}


	@Test
	public void sendOneTimeTransactionToScheduleService() {
		CreateTransactionRequest createTransactionRequest = buildCreateTransactionRequest();
		schedulerServiceSqsAdapter.oneTimeNotification(createTransactionRequest);
		verify(sqsBraveTemplate, times(1)).convertAndSend(Mockito.anyString(), eventArgumentCaptor.capture());
		assertThat(eventArgumentCaptor.getValue().getPayload().getMetadata(),is("metadata"));
	}

	@Test
	public void sendFailedNotificationToScheduleService() {
		TransactionRequest transactionRequest = buildTransactionRequest();
		schedulerServiceSqsAdapter.failedNotification(transactionRequest);
		verify(sqsBraveTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any());
	}

	private CreateTransactionRequest buildCreateTransactionRequest() {
		return CreateTransactionRequest.builder()
				.idClient("idClient")
				.idCredit("idCredit")
				.dayOfPay(15)
				.metadata("metadata")
				.build();
	}

	private TransactionRequest buildTransactionRequest() {
		return TransactionRequest.builder()
				.idClient("idClient")
				.dayOfPay(15)
				.build();
	}
}
