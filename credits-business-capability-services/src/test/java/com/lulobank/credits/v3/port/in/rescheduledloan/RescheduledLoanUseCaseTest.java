package com.lulobank.credits.v3.port.in.rescheduledloan;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.credits.v3.util.EntitiesFactory.RescheduledLoanEventFactory;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RescheduledLoanUseCaseTest {

	private static final String ID_LOAN_ACCOUNT_MAMBU = "YAMW127";
	private static final String ID_LOAN_ACCOUNT_MAMBU_TO_UPDATE = "YAMW128";

	@Mock
	private CreditsV3Repository creditsV3Repository;

	private RescheduledLoanUseCase rescheduledLoanUseCase;

	@Captor
	protected ArgumentCaptor<String> idLoanAccountMambuCaptor;

	@Captor
	protected ArgumentCaptor<CreditsV3Entity> creditsV3EntityCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		rescheduledLoanUseCase = new RescheduledLoanUseCase(creditsV3Repository);
	}

	@Test
	public void processRescheduledEvent_WhenFirstRescheduled() {
		RescheduledLoanMessage rescheduledLoanMessage = RescheduledLoanEventFactory.okRescheduledLoan();
		CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityWithAcceptOffer();

		when(creditsV3Repository.findByIdLoanAccountMambu(idLoanAccountMambuCaptor.capture()))
				.thenReturn(Option.of(creditsV3Entity));
		when(creditsV3Repository.save(creditsV3EntityCaptor.capture())).thenReturn(Try.of(() -> creditsV3Entity));

		Try<Void> response = rescheduledLoanUseCase.execute(rescheduledLoanMessage);

		assertTrue(response.isSuccess());
		assertThat(idLoanAccountMambuCaptor.getValue(), is(ID_LOAN_ACCOUNT_MAMBU));
        assertRescheduledHistoryLength(1);
    }


    @Test
	public void processRescheduledEvent_WhenSecondModification() {
		RescheduledLoanMessage rescheduledLoanMessage = RescheduledLoanEventFactory.okUpdatedLoan();
		CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityAlreadyRescheduled();

		when(creditsV3Repository.findByIdLoanAccountMambu(idLoanAccountMambuCaptor.capture()))
				.thenReturn(Option.of(creditsV3Entity));
		when(creditsV3Repository.save(creditsV3EntityCaptor.capture())).thenReturn(Try.of(() -> creditsV3Entity));

		Try<Void> response = rescheduledLoanUseCase.execute(rescheduledLoanMessage);

		assertTrue(response.isSuccess());
		assertThat(creditsV3Entity.getModifiedHistory().get(1).getIdLoanAccountMambu(),
				is(ID_LOAN_ACCOUNT_MAMBU_TO_UPDATE));
        assertRescheduledHistoryLength(2);
    }

	@Test
	public void processRescheduledEvent_WhenCreditNotFound() {
		RescheduledLoanMessage rescheduledLoanMessage = RescheduledLoanEventFactory.okRescheduledLoan();

		when(creditsV3Repository.findByIdLoanAccountMambu(idLoanAccountMambuCaptor.capture()))
				.thenReturn(Option.none());

		Try<Void> response = rescheduledLoanUseCase.execute(rescheduledLoanMessage);

		assertTrue(response.isSuccess());
		assertThat(idLoanAccountMambuCaptor.getValue(), is(ID_LOAN_ACCOUNT_MAMBU));
		verify(creditsV3Repository, never()).save(any());
	}

	@Test
	public void processRescheduledEvent_WhenSaveCreditFail() {
		RescheduledLoanMessage rescheduledLoanMessage = RescheduledLoanEventFactory.okRescheduledLoan();
		CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityWithAcceptOffer();

		when(creditsV3Repository.findByIdLoanAccountMambu(idLoanAccountMambuCaptor.capture()))
				.thenReturn(Option.of(creditsV3Entity));
		when(creditsV3Repository.save(creditsV3EntityCaptor.capture())).thenThrow(new RuntimeException("Error..."));

		Try<Void> response = rescheduledLoanUseCase.execute(rescheduledLoanMessage);

		assertTrue(response.isSuccess());
		assertNull(response.get());
	}

    private void assertRescheduledHistoryLength(int historyLength) {
        assertThat(creditsV3EntityCaptor.getValue().getModifiedHistory().size(), is(historyLength));
    }
}
