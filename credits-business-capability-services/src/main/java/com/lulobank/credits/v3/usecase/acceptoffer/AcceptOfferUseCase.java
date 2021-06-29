package com.lulobank.credits.v3.usecase.acceptoffer;

import com.lulobank.credits.v3.dto.ClientInformationV3;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.dto.DocumentIdV3;
import com.lulobank.credits.v3.dto.LoanRequestedV3;
import com.lulobank.credits.v3.dto.PhoneV3;
import com.lulobank.credits.v3.mapper.CreditsV3EntityMapper;
import com.lulobank.credits.v3.port.in.loan.LoanFactory;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.clients.ClientService;
import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse;
import com.lulobank.credits.v3.port.out.otp.ValidateOtpService;
import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpRequest;
import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpResponse;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferService;
import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest;
import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest.ProductOfferStatus;
import com.lulobank.credits.v3.port.out.promissorynote.PromissoryNoteAsyncService;
import com.lulobank.credits.v3.port.out.promissorynote.dto.PromissoryNoteAsyncServiceRequest;
import com.lulobank.credits.v3.port.out.saving.SavingAccountError;
import com.lulobank.credits.v3.port.out.saving.SavingAccountService;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.service.PreApproveOfferService;
import com.lulobank.credits.v3.usecase.acceptoffer.command.AcceptOfferCommand;
import com.lulobank.credits.v3.usecase.acceptoffer.command.AcceptOfferUseCaseResponse;
import com.lulobank.credits.v3.util.LogMapper;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.CustomLog;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@CustomLog
public class AcceptOfferUseCase implements UseCase<AcceptOfferCommand, Either<UseCaseResponseError, AcceptOfferUseCaseResponse>> {

    private static final String CREDIT_NOT_FOUND_MESSAGE = "Credit not found, idClient : %s ";
    private static final String ERROR_OFFER_NOT_FOUND_MESSAGE = "Offer not found, idClient : %s";
    private static final String ERROR_CREATING_LOAN_IN_PROVIDER_MESSAGE = "Error creating loan in provider for client, idClient : %s";

    private final PromissoryNoteAsyncService promissoryNoteAsyncService;
    private final CreditsV3Repository creditsV3Repository;
    private final PreApproveOfferService preApproveOfferService;
    private final CreditsConditionV3 creditsConditionV3;
    private final LoanV3Service loanV3Service;
    private final SavingAccountService savingAccountService;
    private final ProductOfferService productOfferService;
    private final ValidateOtpService validateOtpService;
    private final ClientService clientService;

    public AcceptOfferUseCase(PromissoryNoteAsyncService promissoryNoteAsyncService,
                                  CreditsV3Repository creditsV3Repository,
                                  PreApproveOfferService preApproveOfferService, 
                                  CreditsConditionV3 creditsConditionV3,
                                  LoanV3Service loanV3Service, 
                                  SavingAccountService savingAccountService,
                                  ProductOfferService productOfferService,
                                  ValidateOtpService validateOtpService,
                                  ClientService clientService) {
        this.promissoryNoteAsyncService = promissoryNoteAsyncService;
        this.creditsV3Repository = creditsV3Repository;
        this.preApproveOfferService = preApproveOfferService;
        this.creditsConditionV3 = creditsConditionV3;
        this.loanV3Service = loanV3Service;
        this.savingAccountService = savingAccountService;
        this.productOfferService = productOfferService;
        this.validateOtpService = validateOtpService;
        this.clientService = clientService;
    }

    public Either<UseCaseResponseError, AcceptOfferUseCaseResponse> execute(AcceptOfferCommand acceptOfferCommand) {
    	log.info("[AcceptOfferUseCase] Start process to accept offer, idClient: {} ", acceptOfferCommand.getIdClient());
        
    	return isValidSign(acceptOfferCommand)
        	.filterOrElse(ValidateOtpResponse::isValid, isValidFalse -> CreditsError.validateOtpError())
        	.flatMap(v -> findCreditEntity(acceptOfferCommand))
        	.flatMap(creditsV3Entity -> mapOffer(acceptOfferCommand, creditsV3Entity))
        	.flatMap(this::createLoanTransaction)
        	.flatMap(loanTransaction -> setClientInfo(loanTransaction, acceptOfferCommand.getCredentials().getHeaders()))
        	.flatMap(loanTransaction -> setSavingAccountInfo(loanTransaction, acceptOfferCommand.getCredentials().getHeaders()))
        	.flatMap(this::creteLoan)
        	.map(CreditsV3EntityMapper::mapLoanInformation)
        	.peek(transaction -> creditsV3Repository.save(transaction.getEntity()))
        	.peek(transaction -> closeProductOffer(transaction, acceptOfferCommand))
        	.peek(transaction -> promissoryNoteAsyncService.createPromissoryNote(transaction, buildPromissoryNoteAsyncServiceRequest(acceptOfferCommand)))
        	.map(transaction -> new AcceptOfferUseCaseResponse(true));
    }


	private Either<UseCaseResponseError, ValidateOtpResponse> isValidSign(AcceptOfferCommand acceptOfferCommand) {
        return validateOtpService.validateOtp(ValidateOtpRequest.builder()
        		.auth(acceptOfferCommand.getCredentials().getHeaders())
        		.idClient(acceptOfferCommand.getIdClient())
        		.idCredit(acceptOfferCommand.getIdCredit())
        		.idOffer(acceptOfferCommand.getIdOffer())
        		.otp(acceptOfferCommand.getConfirmationLoanOTP())
        		.build());
    }
    
    private PromissoryNoteAsyncServiceRequest buildPromissoryNoteAsyncServiceRequest(AcceptOfferCommand acceptOfferCommand) {
    	return PromissoryNoteAsyncServiceRequest.builder()
    			.credentials(acceptOfferCommand.getCredentials())
    			.confirmationLoanOTP(acceptOfferCommand.getConfirmationLoanOTP())
    			.build();
    }
    
	private void closeProductOffer(LoanTransaction transaction, AcceptOfferCommand acceptOfferCommand) {
		Option.of(acceptOfferCommand.getIdProductOffer()).onEmpty(() -> log.info("idProductOffer is missing"))
				.flatMap(idProductOffer -> productOfferService
						.updateProductOffer(buildProductOfferRequest(transaction.getEntity(), idProductOffer),
								acceptOfferCommand.getCredentials().getHeaders())
						.peekLeft(
								error -> log.error(String.format("Error trying to close productOffer: %s, idClient: %s Error: %s",
										idProductOffer, transaction.getEntity().getIdClient(), error.getBusinessCode())))
						.toOption());
	}
    
	private ProductOfferRequest buildProductOfferRequest(CreditsV3Entity creditsV3Entity, String idProductOffer) {
		ProductOfferRequest productOfferRequest = new ProductOfferRequest();
		productOfferRequest.setIdClient(creditsV3Entity.getIdClient());
		productOfferRequest.setIdProductOffer(idProductOffer);
		productOfferRequest.setStatus(ProductOfferStatus.CLOSED);
		return productOfferRequest;
	}
	
	private Either<UseCaseResponseError, LoanTransaction> setClientInfo(LoanTransaction transaction,
			Map<String, String> auth) {
		
		return clientService.getClientInformation(transaction.getEntity().getIdClient(), auth)
				.peekLeft(error -> log.error("Error getting client information data: " + error.getDetail()))
				.map(this::buildClientInformationV3)
				.map(clientInformationV3 -> {
					transaction.getEntity().setClientInformation(clientInformationV3);
					return transaction;
				})
				.toEither(CreditsError::errorWithClientInfo);
	}
	
	private ClientInformationV3 buildClientInformationV3(ClientInformationResponse clientInformationResponse) {
		DocumentIdV3 documentIdV3 = new DocumentIdV3();
		documentIdV3.setId(clientInformationResponse.getDocumentNumber());
		documentIdV3.setType(clientInformationResponse.getDocumentType());
		documentIdV3.setIssueDate(clientInformationResponse.getExpeditionDate());
		ClientInformationV3 clientInformationV3 = new ClientInformationV3();
		clientInformationV3.setDocumentId(documentIdV3);
		clientInformationV3.setEmail(clientInformationResponse.getEmail());
		clientInformationV3.setGender(clientInformationResponse.getGender());
		clientInformationV3.setName(clientInformationResponse.getClientAdditionalPersonalInfo().getFirstName());
		clientInformationV3.setMiddleName(clientInformationResponse.getClientAdditionalPersonalInfo().getSecondName());
		clientInformationV3.setLastName(clientInformationResponse.getClientAdditionalPersonalInfo().getFirstSurname());
		clientInformationV3.setSecondSurname(clientInformationResponse.getClientAdditionalPersonalInfo().getSecondSurname());
		clientInformationV3.setPhone(buildPhoneV3(clientInformationResponse));
		return clientInformationV3;
	}

	private PhoneV3 buildPhoneV3(ClientInformationResponse clientInformationResponse) {
		PhoneV3 phoneV3 = new PhoneV3();
		Option.of(clientInformationResponse.getPhone())
			.peek(phone -> {
				phoneV3.setNumber(phone.getNumber());
				phoneV3.setPrefix(String.valueOf(phone.getPrefix()));
			});
		return phoneV3;
	}

	private Either<UseCaseResponseError, LoanTransaction> setSavingAccountInfo(LoanTransaction transaction,
			Map<String, String> auth) {
		return savingAccountService.getSavingAccount(transaction.getEntity().getIdClient(), auth)
				.peekLeft(error -> log.error("Error getting saving account data: " + error.getDetail()))
				.flatMap(getSavingAccountTypeResponse -> 
						clientService.getClientInformation(transaction.getEntity().getIdClient(), auth)
						.peekLeft(error -> log.error("Error getting client information data: " + error.getDetail()))
						.map(clientInformationResponse -> buildSavingsAccountResponse(
								getSavingAccountTypeResponse.getIdSavingAccount(),
								clientInformationResponse.getDocumentNumber())))
				.map(transaction::setSavingsAccountResponse).toEither(SavingAccountError::errorGettingData);
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
				.toEither(CreditsError::errorCreatingLoanProvider);
	}

    private Either<UseCaseResponseError, LoanTransaction> createLoanTransaction(CreditsV3Entity creditsV3Entity) {
        return Either.right(new LoanTransaction().setCreditsV3Entity(creditsV3Entity));
    }

    private Either<UseCaseResponseError, CreditsV3Entity> mapOffer(AcceptOfferCommand acceptOfferCommand, CreditsV3Entity creditsV3Entity) {
        return preApproveOfferService.getOffer(creditsV3Entity, acceptOfferCommand).map(offer -> {
            creditsV3Entity.setAcceptOffer(offer);
            creditsV3Entity.setAutomaticDebit(acceptOfferCommand.isAutomaticDebitPayments());
            creditsV3Entity.setDayOfPay(acceptOfferCommand.getDayOfPay());
            creditsV3Entity.setAcceptDate(LocalDateTime.now());
            creditsV3Entity.setLoanRequested(buildLoanRequest(acceptOfferCommand.getLoanPurpose()));
            return creditsV3Entity;
        }).onEmpty(() -> log.info(String.format(ERROR_OFFER_NOT_FOUND_MESSAGE, acceptOfferCommand.getIdClient())))
                .toEither(CreditsError::idOfferNotFound);
    }

    private LoanRequestedV3 buildLoanRequest(String loanPurpose) {
    	LoanRequestedV3 loanRequestedV3 = new LoanRequestedV3();
    	loanRequestedV3.setPurpose(loanPurpose);
		return loanRequestedV3;
	}

	private Either<UseCaseResponseError, CreditsV3Entity> findCreditEntity(AcceptOfferCommand acceptOfferCommand) {
        return creditsV3Repository
                .findClientByOffer(UUID.fromString(acceptOfferCommand.getIdCredit()), acceptOfferCommand.getIdClient())
                .onEmpty(() -> log.info(String.format(CREDIT_NOT_FOUND_MESSAGE, acceptOfferCommand.getIdClient())))
                .toEither(CreditsError::idCreditNotFound);
    }
}
