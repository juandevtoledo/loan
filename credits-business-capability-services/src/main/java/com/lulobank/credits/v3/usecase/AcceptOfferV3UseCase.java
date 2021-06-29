package com.lulobank.credits.v3.usecase;

import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.mapper.CreditsV3EntityMapper;
import com.lulobank.credits.v3.port.in.loan.LoanFactory;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.in.promissorynote.ValidForPromissoryNoteSing;
import com.lulobank.credits.v3.port.in.promissorynote.dto.SignPromissoryNoteResponse;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.promissorynote.PromissoryNoteAsyncService;
import com.lulobank.credits.v3.port.out.saving.SavingAccountError;
import com.lulobank.credits.v3.port.out.saving.SavingAccountService;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.service.OfferService;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.credits.v3.util.LogMapper;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;
import lombok.CustomLog;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@CustomLog
public class AcceptOfferV3UseCase implements UseCase<AcceptOffer, Either<UseCaseResponseError, SignPromissoryNoteResponse>> {

	private static final String CREDIT_NOT_FOUND_MESSAGE = "Credit not found, idClient : %s ";
	private static final String ERROR_OFFER_NOT_FOUND_MESSAGE = "Offer not found, idClient : %s";
	private static final String ERROR_CREATING_LOAN_IN_PROVIDER_MESSAGE = "Error creating loan in provider for client, idClient : %s";

	private final ValidForPromissoryNoteSing validForPromissoryNoteSing;
	private final PromissoryNoteAsyncService promissoryNoteAsyncService;
	private final CreditsV3Repository creditsV3Repository;
	private final OfferService offerService;
	private final CreditsConditionV3 creditsConditionV3;
	private final LoanV3Service loanV3Service;
	private final SavingAccountService savingAccountService;

	public AcceptOfferV3UseCase(ValidForPromissoryNoteSing validForPromissoryNoteSing,
			PromissoryNoteAsyncService promissoryNoteAsyncService, CreditsV3Repository creditsV3Repository,
			OfferService offerService, CreditsConditionV3 creditsConditionV3, LoanV3Service loanV3Service,
			SavingAccountService savingAccountService) {
		this.validForPromissoryNoteSing = validForPromissoryNoteSing;
		this.promissoryNoteAsyncService = promissoryNoteAsyncService;
		this.creditsV3Repository = creditsV3Repository;
		this.offerService = offerService;
		this.creditsConditionV3 = creditsConditionV3;
		this.loanV3Service = loanV3Service;
		this.savingAccountService = savingAccountService;
	}

	public Either<UseCaseResponseError, SignPromissoryNoteResponse> execute(AcceptOffer acceptOffer) {
		log.info("Start process to create loan, idClient: {} ", acceptOffer.getIdClient());
		return isValidSign(acceptOffer)
				.filterOrElse(SignPromissoryNoteResponse::isValid, isValidFalse -> CreditsError.validateOtpError())
				.flatMap(v -> findCreditByClientOffer(acceptOffer))
				.flatMap(creditsV3Entity -> mapOffer(acceptOffer, creditsV3Entity))
				.map(this::createLoanTransaction)
				.flatMap(loanTransaction -> setSavingAccountInfo(loanTransaction,
						acceptOffer.getCredentials().getHeaders()))
				.flatMap(this::creteLoan)
				.map(CreditsV3EntityMapper::mapLoanInformation)
				.peek(transaction -> creditsV3Repository.save(transaction.getEntity()))
				.peek(transaction -> promissoryNoteAsyncService.createPromissoryNote(transaction, acceptOffer))
				.map(transaction -> new SignPromissoryNoteResponse(true));
				
	}

	private Either<UseCaseResponseError, SignPromissoryNoteResponse> isValidSign(AcceptOffer offer) {
		return validForPromissoryNoteSing.execute(offer, offer.getCredentials().getHeaders())
				.toEither(() -> CreditsError.validateOtpError());
	}

	private Either<UseCaseResponseError, LoanTransaction> setSavingAccountInfo(LoanTransaction transaction, Map<String, String> auth) {

		return savingAccountService.getSavingAccount(transaction.getEntity().getIdClient(), auth)
				.peekLeft(error -> log.error("Error getting saving account data: " + error.getDetail()))
				.map(getSavingAccountTypeResponse -> buildSavingsAccountResponse(
						getSavingAccountTypeResponse.getIdSavingAccount(),
						transaction.getEntity().getClientInformation().getDocumentId().getId()))
				.map(transaction::setSavingsAccountResponse)
				.toEither(() -> SavingAccountError.errorGettingData());
	}

	private SavingsAccountResponse buildSavingsAccountResponse(String accountId, String idCbs) {
		SavingsAccountResponse savingsAccountResponse = new SavingsAccountResponse();
		savingsAccountResponse.setAccountId(accountId);
		savingsAccountResponse.setIdCbs(idCbs);
		return savingsAccountResponse;
	}

	private Either<UseCaseResponseError, LoanTransaction> creteLoan(LoanTransaction transaction) {
		LoanRequest loanRequest = LoanFactory.createLoanRequest(transaction, creditsConditionV3);
		return loanV3Service.create(loanRequest)
				.peek(response -> log.info("Loan Created, idClient : {}, response: {} ",
						transaction.getEntity().getIdClient(), LogMapper.getJson(response)))
				.map(transaction::setLoanResponse)
				.onFailure(error -> log.error(
						String.format(ERROR_CREATING_LOAN_IN_PROVIDER_MESSAGE, transaction.getEntity().getIdClient()),
						error))
				.toEither(() -> CreditsError.errorCreatingLoanProvider());
	}

	private LoanTransaction createLoanTransaction(CreditsV3Entity creditsV3Entity) {
		return new LoanTransaction().setCreditsV3Entity(creditsV3Entity);
	}

	private Either<UseCaseResponseError, CreditsV3Entity> mapOffer(AcceptOffer acceptOffer,
			CreditsV3Entity creditsV3Entity) {
		return offerService.getOffer(creditsV3Entity, acceptOffer).map(offer -> {
			creditsV3Entity.setAcceptOffer(offer);
			creditsV3Entity.setAutomaticDebit(acceptOffer.isAutomaticDebitPayments());
			creditsV3Entity.setDayOfPay(acceptOffer.getDayOfPay());
			creditsV3Entity.setAcceptDate(LocalDateTime.now());
			return creditsV3Entity;
		}).onEmpty(() -> log.info(String.format(ERROR_OFFER_NOT_FOUND_MESSAGE, acceptOffer.getIdClient())))
				.toEither(() -> CreditsError.idOfferNotFound());
	}

	private Either<UseCaseResponseError, CreditsV3Entity> findCreditByClientOffer(AcceptOffer acceptOffer) {
		return creditsV3Repository
				.findClientByOffer(UUID.fromString(acceptOffer.getIdCredit()), acceptOffer.getIdClient())
				.onEmpty(() -> log.info(String.format(CREDIT_NOT_FOUND_MESSAGE, acceptOffer.getIdClient())))
				.toEither(() -> CreditsError.idCreditNotFound());
	}
}
