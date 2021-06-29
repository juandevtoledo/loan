package com.lulobank.credits.v3.port.in.digitalevidence;

import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.loan.dto.DisbursementLoanRequest;
import com.lulobank.credits.v3.port.in.loan.dto.LoanV3Error;
import com.lulobank.credits.v3.port.out.ClientAlertsAsyncService;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.NotificationV3Service;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.CreateTransactionRequest;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory;
import com.lulobank.credits.v3.util.EntitiesFactory.DigitalEvidenceEvent;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.credits.v3.port.in.loan.LoanState.APPROVED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DigitalEvidenceCreatedUseCaseTest {

	@Mock
	private CreditsV3Repository creditsV3Repository;
	@Mock
	private LoanV3Service loanV3Service;
	@Mock
	private NotificationV3Service notificationV3Service;
	@Mock
	private ClientAlertsAsyncService clientAlertsAsyncService;
	@Mock
	private SchedulerTransactionAsyncService schedulerAutomaticDebitAsyncService;

	private DigitalEvidenceCreatedUseCase subject;

	@Captor
	protected ArgumentCaptor<DisbursementLoanRequest> disbursementLoanRequestCaptor;
	@Captor
	protected ArgumentCaptor<CreateTransactionRequest> createTransactionRequestCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		subject = new DigitalEvidenceCreatedUseCase(creditsV3Repository, loanV3Service,
				notificationV3Service,clientAlertsAsyncService, schedulerAutomaticDebitAsyncService);
	}

	@Test
	public void processTransactionSuccessful() {

		DigitalEvidenceCreatedMessage digitalEvidenceCreatedMessage = DigitalEvidenceEvent
				.buildDigitalEvidenceCreatedMessage(true);
		CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityWithAcceptOffer();

		when(creditsV3Repository.findById(digitalEvidenceCreatedMessage.getIdCredit())).thenReturn(Option.of(creditsV3Entity));
		when(loanV3Service.disbursementLoan(any(DisbursementLoanRequest.class)))
				.thenReturn(Either.right(APPROVED.name()));

		Try<Void> response = subject.execute(digitalEvidenceCreatedMessage);

		assertEquals(response.isSuccess(), true);
		verify(creditsV3Repository).save(any());
		verify(notificationV3Service).loanCreatedNotification(any());
		verify(clientAlertsAsyncService).sendCreditFinishedNotification(any(LoanTransaction.class));
		verify(schedulerAutomaticDebitAsyncService).createTransaction(createTransactionRequestCaptor.capture());
		assertThatCreateTransactionRequest(createTransactionRequestCaptor.getValue());
	}

	@Test
	public void processTransactionLoanCreateFaild() {

		DigitalEvidenceCreatedMessage digitalEvidenceCreatedMessage = DigitalEvidenceEvent
				.buildDigitalEvidenceCreatedMessage(true);
		CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityWithAcceptOffer();

		when(creditsV3Repository.findById(digitalEvidenceCreatedMessage.getIdCredit())).thenReturn(Option.of(creditsV3Entity));
		when(loanV3Service.disbursementLoan(any(DisbursementLoanRequest.class)))
				.thenReturn(Either.left(new LoanV3Error("", "")));

		Try<Void> response = subject.execute(digitalEvidenceCreatedMessage);

		assertEquals(response.isFailure(), true);
		verify(creditsV3Repository, times(0)).save(any());
		verify(notificationV3Service, times(0)).loanCreatedNotification(any());
        verify(clientAlertsAsyncService, times(0)).sendCreditFinishedNotification(any(LoanTransaction.class));
		verify(loanV3Service, times(1)).disbursementLoan(disbursementLoanRequestCaptor.capture());
		verify(schedulerAutomaticDebitAsyncService, never()).createTransaction(any());
	}

	@Test
	public void processTransactionCreditNotFound() {

		DigitalEvidenceCreatedMessage digitalEvidenceCreatedMessage = DigitalEvidenceEvent
				.buildDigitalEvidenceCreatedMessage(true);

		when(creditsV3Repository.findById(digitalEvidenceCreatedMessage.getIdCredit())).thenReturn(Option.none());

		Try<Void> response = subject.execute(digitalEvidenceCreatedMessage);

		assertEquals(response.isFailure(), true);
		verify(loanV3Service, times(0)).disbursementLoan(any());
		verify(creditsV3Repository, times(0)).save(any());
		verify(notificationV3Service, times(0)).loanCreatedNotification(any());
        verify(clientAlertsAsyncService, times(0)).sendCreditFinishedNotification(any(LoanTransaction.class));
        verify(schedulerAutomaticDebitAsyncService,never()).createTransaction(any());
	}

	@Test
	public void processTransactionDigitalEvidenceMessageSuccessFalse() {

		DigitalEvidenceCreatedMessage digitalEvidenceCreatedMessage = DigitalEvidenceEvent
				.buildDigitalEvidenceCreatedMessage(false);

		Try<Void> response = subject.execute(digitalEvidenceCreatedMessage);

		assertEquals(response.isFailure(), true);
		verify(loanV3Service, times(0)).disbursementLoan(any());
		verify(creditsV3Repository, times(0)).save(any());
		verify(notificationV3Service, times(0)).loanCreatedNotification(any());
        verify(clientAlertsAsyncService, times(0)).sendCreditFinishedNotification(any(LoanTransaction.class));
        verify(schedulerAutomaticDebitAsyncService, never()).createTransaction(any());
	}

	private void assertThatCreateTransactionRequest(CreateTransactionRequest createTransactionRequest) {
		assertThat(createTransactionRequest.getDayOfPay(), is(15));
		assertThat(createTransactionRequest.getMetadata(), is("15#credits#SUBSCRIPTION"));
		assertThat(createTransactionRequest.getIdCredit(), is("014176ef-e291-4db6-9b49-18d253a8ae5d"));
		assertThat(createTransactionRequest.getIdClient(), is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
	}
}