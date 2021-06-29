package com.lulobank.credits.v3.port.in.promissorynote;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.NotificationV3Service;
import com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory;

import io.vavr.control.Option;
import io.vavr.control.Try;

public class PromissoryNoteCreatedUseCaseTest {

	private PromissoryNoteCreatedUseCase subject;

	@Mock
	private CreditsV3Repository creditsV3Repository;
	@Mock
	private NotificationV3Service notificationV3Service;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		subject = new PromissoryNoteCreatedUseCase(creditsV3Repository,
				notificationV3Service);
	}

	@Test
	public void processTransactionSuccessful() {
		CreatePromissoryNoteResponseMessage createPromissoryNoteResponseMessage = buildCreatePromissoryNoteResponseMessage();
		CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityWithAcceptOffer();
		when(creditsV3Repository.findById(createPromissoryNoteResponseMessage.getIdCredit())).thenReturn(Option.of(creditsV3Entity));
		Try<Void> response = subject.execute(createPromissoryNoteResponseMessage);
		assertEquals(response.isSuccess(), true);
		verify(notificationV3Service).requestDigitalEvidence(any(), any());
	}
	
	@Test
	public void processTransactionCreditNotFound() {
		CreatePromissoryNoteResponseMessage createPromissoryNoteResponseMessage = buildCreatePromissoryNoteResponseMessage();

		when(creditsV3Repository.findById(createPromissoryNoteResponseMessage.getIdCredit())).thenReturn(Option.none());
		
		Try<Void> response = subject.execute(createPromissoryNoteResponseMessage);
		assertEquals(response.isSuccess(), false);
		verify(notificationV3Service, times(0)).requestDigitalEvidence(any(), any());
	}

	private CreatePromissoryNoteResponseMessage buildCreatePromissoryNoteResponseMessage() {
		CreatePromissoryNoteResponseMessage createPromissoryNoteResponseMessage = new CreatePromissoryNoteResponseMessage();
		createPromissoryNoteResponseMessage.setClientAccountId(12356);
		createPromissoryNoteResponseMessage.setIdClient("idClient");
		createPromissoryNoteResponseMessage.setIdCredit("5a9c7d82-393c-4c05-9b3f-35adea480f16");
		createPromissoryNoteResponseMessage.setPromissoryNoteId(4365345);
		createPromissoryNoteResponseMessage.setSignPassword("signPassword");
		createPromissoryNoteResponseMessage.setHeaders(new HashMap<String, String>());
		createPromissoryNoteResponseMessage.setHeadersToSQS(new HashMap<String, Object>());
		return createPromissoryNoteResponseMessage;
	}
}
