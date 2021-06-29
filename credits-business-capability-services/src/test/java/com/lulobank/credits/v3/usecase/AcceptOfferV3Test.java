package com.lulobank.credits.v3.usecase;

import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.in.promissorynote.ValidForPromissoryNoteSing;
import com.lulobank.credits.v3.port.in.promissorynote.dto.SignPromissoryNoteResponse;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.promissorynote.PromissoryNoteAsyncService;
import com.lulobank.credits.v3.port.out.saving.SavingAccountService;
import com.lulobank.credits.v3.port.out.saving.dto.GetSavingAcountTypeResponse;
import com.lulobank.credits.v3.service.OfferService;
import com.lulobank.credits.v3.usecase.AcceptOfferV3UseCase;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsCondition.createCreditsCondition;
import static com.lulobank.credits.v3.util.EntitiesFactory.AcceptOfferFactory.createAcceptOfferWithOfferValid;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.foundCreditsEntityInBD;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static com.lulobank.credits.v3.util.EntitiesFactory.OfferFactory.createOfferEntityV3Valid;
import static com.lulobank.credits.v3.util.EntitiesFactory.LoanFactory.createLoanResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class AcceptOfferV3Test {

	private CreditsConditionV3 creditsConditionV3 = createCreditsCondition();
	@Mock
	private PromissoryNoteAsyncService promissoryNoteAsyncService;
	@Mock
	private CreditsV3Repository creditsV3Repository;
	@Mock
	private OfferService offerService;
	@Mock
	private LoanV3Service loanV3Service;
	@Mock
	private SavingAccountService savingAccountService;
	@Mock
	private ValidForPromissoryNoteSing validForPromissoryNoteSing;

	private AcceptOfferV3UseCase acceptOfferV3UseCase;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		acceptOfferV3UseCase = new AcceptOfferV3UseCase(validForPromissoryNoteSing, promissoryNoteAsyncService, creditsV3Repository,
				offerService, creditsConditionV3, loanV3Service, savingAccountService);
	}

	@Test
	public void processTransactionWhenCreditNotFound() {
		AcceptOffer acceptOffer = createAcceptOfferWithOfferValid();
		when(validForPromissoryNoteSing.execute(Mockito.any(), Mockito.any()))
				.thenReturn(Try.of(() -> new SignPromissoryNoteResponse(true)));
		when(creditsV3Repository.findClientByOffer(UUID.fromString(acceptOffer.getIdCredit()),
				acceptOffer.getIdClient())).thenReturn(Option.none());
		Either<UseCaseResponseError, SignPromissoryNoteResponse> execute = acceptOfferV3UseCase.execute(acceptOffer);
		assertThat(execute.isEmpty(), is(true));
		assertThat(execute.isLeft(), is(true));
		assertThat(execute.getLeft().getBusinessCode(), is(CreditsError.idCreditNotFound().getBusinessCode()));
	}

	@Test
	public void processTransactionWhenCreditFoundButNoOffer() {
		AcceptOffer acceptOffer = createAcceptOfferWithOfferValid();
		CreditsV3Entity entity = foundCreditsEntityInBD();
		when(validForPromissoryNoteSing.execute(Mockito.any(), Mockito.any()))
				.thenReturn(Try.of(() -> new SignPromissoryNoteResponse(true)));
		when(creditsV3Repository.findClientByOffer(UUID.fromString(acceptOffer.getIdCredit()),
				acceptOffer.getIdClient())).thenReturn(Option.of(entity));
		when(offerService.getOffer(entity, acceptOffer)).thenReturn(Option.none());
		Either<UseCaseResponseError, SignPromissoryNoteResponse> execute = acceptOfferV3UseCase.execute(acceptOffer);
		assertThat(execute.isEmpty(), is(true));
		assertThat(execute.isLeft(), is(true));
		assertThat(execute.getLeft().getBusinessCode(), is(CreditsError.idOfferNotFound().getBusinessCode()));
	}

	@Test
	public void processTransactionWhenCreditFoundAndSuccess() {
		AcceptOffer acceptOffer = createAcceptOfferWithOfferValid();
		CreditsV3Entity entity = creditsEntityWithAcceptOffer();
		OfferEntityV3 offerEntityV3 = createOfferEntityV3Valid();
		GetSavingAcountTypeResponse getSavingAcountTypeResponse = buildGetSavingAcountTypeResponse();
		when(validForPromissoryNoteSing.execute(Mockito.any(), Mockito.any()))
				.thenReturn(Try.of(() -> new SignPromissoryNoteResponse(true)));
		when(creditsV3Repository.findClientByOffer(UUID.fromString(acceptOffer.getIdCredit()),
				acceptOffer.getIdClient())).thenReturn(Option.of(entity));
		when(loanV3Service.create(any(LoanRequest.class)))
				.thenReturn(Try.of(() -> createLoanResponse()));
		when(offerService.getOffer(entity, acceptOffer)).thenReturn(Option.of(offerEntityV3));
		when(savingAccountService.getSavingAccount(eq(entity.getIdClient()), any()))
				.thenReturn(Either.right(getSavingAcountTypeResponse));
		Either<UseCaseResponseError, SignPromissoryNoteResponse> execute = acceptOfferV3UseCase.execute(acceptOffer);
		assertThat(execute.isRight(), is(true));
		verify(promissoryNoteAsyncService).createPromissoryNote(Mockito.any(), Mockito.any(AcceptOffer.class));
	}

	private GetSavingAcountTypeResponse buildGetSavingAcountTypeResponse() {
		return GetSavingAcountTypeResponse.builder().idSavingAccount("idSavingAccount").build();
	}
}
