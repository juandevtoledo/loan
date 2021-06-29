package com.lulobank.credits.v3.usecase.acceptoffer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.clients.ClientService;
import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse;
import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse.ClientAdditionalPersonalInfo;
import com.lulobank.credits.v3.port.out.otp.ValidateOtpService;
import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpRequest;
import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpResponse;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferService;
import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest;
import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest.ProductOfferStatus;
import com.lulobank.credits.v3.port.out.promissorynote.PromissoryNoteAsyncService;
import com.lulobank.credits.v3.port.out.promissorynote.dto.PromissoryNoteAsyncServiceRequest;
import com.lulobank.credits.v3.port.out.saving.SavingAccountService;
import com.lulobank.credits.v3.port.out.saving.dto.GetSavingAcountTypeResponse;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.service.PreApproveOfferService;
import com.lulobank.credits.v3.usecase.acceptoffer.AcceptOfferUseCase;
import com.lulobank.credits.v3.usecase.acceptoffer.command.AcceptOfferCommand;
import com.lulobank.credits.v3.usecase.acceptoffer.command.AcceptOfferUseCaseResponse;
import com.lulobank.credits.v3.util.EntitiesFactory;
import com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory;
import com.lulobank.credits.v3.util.EntitiesFactory.OfferFactory;
import com.lulobank.credits.v3.vo.AdapterCredentials;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class AcceptOfferUseCaseTest {

	private CreditsConditionV3 creditsConditionV3 = EntitiesFactory.CreditsCondition.createCreditsCondition();
	@Mock
	private PromissoryNoteAsyncService promissoryNoteAsyncService;
	@Mock
	private CreditsV3Repository creditsV3Repository;
	@Mock
	private PreApproveOfferService preApproveOfferService;
	@Mock
	private LoanV3Service loanV3Service;
	@Mock
	private SavingAccountService savingAccountService;
	@Mock
	private ProductOfferService productOfferService;
	@Mock
	private ValidateOtpService validateOtpService;
	@Mock
	private ClientService clientService;

	private AcceptOfferUseCase acceptOfferUseCase;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		acceptOfferUseCase = new AcceptOfferUseCase(promissoryNoteAsyncService, creditsV3Repository, preApproveOfferService,
				creditsConditionV3, loanV3Service, savingAccountService, productOfferService, validateOtpService, clientService);
	}
	
	@Test
	public void serviceShouldReturnSuccess() {
		AcceptOfferCommand acceptOfferCommand = buildAcceptOfferCommand();
		CreditsV3Entity entity = CreditsEntityFactory.creditsEntityWithAcceptOffer();
		OfferEntityV3 offerEntityV3 = OfferFactory.createOfferEntityV3Valid();
		GetSavingAcountTypeResponse getSavingAcountTypeResponse = buildGetSavingAcountTypeResponse();
		ProductOfferRequest productOfferRequest = buildProductOfferRequest(entity, acceptOfferCommand.getIdProductOffer());
		ValidateOtpRequest validateOtpRequest = buildValidateOtpRequest(acceptOfferCommand);
		ValidateOtpResponse validateOtpResponse = buildValidateOtpResponse(true);
		ClientInformationResponse clientInformationResponse = buildClientInformationResponse();
		when(validateOtpService.validateOtp(refEq(validateOtpRequest, "credentials"))).thenReturn(Either.right(validateOtpResponse));
        when(creditsV3Repository.findClientByOffer(UUID.fromString(acceptOfferCommand.getIdCredit()), acceptOfferCommand.getIdClient())).thenReturn(Option.of(entity));
        when(loanV3Service.create(any(LoanRequest.class))).thenReturn(Try.of(() -> EntitiesFactory.LoanFactory.createLoanResponse()));
        when(preApproveOfferService.getOffer(entity, acceptOfferCommand)).thenReturn(Option.of(offerEntityV3));
        when(savingAccountService.getSavingAccount(eq(entity.getIdClient()), any())).thenReturn(Either.right(getSavingAcountTypeResponse));
        when(productOfferService.updateProductOffer(any(ProductOfferRequest.class), any())).thenReturn(Either.right(productOfferRequest));
        when(clientService.getClientInformation(eq(entity.getIdClient()), any())).thenReturn(Either.right(clientInformationResponse));
        Either<UseCaseResponseError, AcceptOfferUseCaseResponse> execute = acceptOfferUseCase.execute(acceptOfferCommand);
        assertThat(execute.isRight(), is(true));
        verify(promissoryNoteAsyncService).createPromissoryNote(Mockito.any(), Mockito.any(PromissoryNoteAsyncServiceRequest.class));
	}
	

	@Test
	public void AcceptOfferShouldReturnError() {
		AcceptOfferCommand command = buildAcceptOfferCommand();
		ValidateOtpRequest validateOtpRequest = buildValidateOtpRequest(command);
		UseCaseResponseError error = new UseCaseResponseError("businessCode", "providerCode", "detail"); 
		
		when(validateOtpService.validateOtp(refEq(validateOtpRequest, "credentials"))).thenReturn(Either.left(error));
		Either<UseCaseResponseError, AcceptOfferUseCaseResponse> response = acceptOfferUseCase.execute(command);
		assertThat(response.isLeft(), is(true));
	}
	
	private ClientInformationResponse buildClientInformationResponse() {
		return ClientInformationResponse.builder()
				.documentNumber("documentNumber")
				.clientAdditionalPersonalInfo(ClientAdditionalPersonalInfo.builder().build())
				.build();
	}

	private ValidateOtpResponse buildValidateOtpResponse(boolean valid) {
		return ValidateOtpResponse.builder()
				.isValid(valid)
				.build();
	}
	
	private ValidateOtpRequest buildValidateOtpRequest(AcceptOfferCommand acceptOfferCommand) {
		return ValidateOtpRequest.builder()
				.auth(acceptOfferCommand.getCredentials().getHeaders())
				.idClient(acceptOfferCommand.getIdClient())
				.idCredit(acceptOfferCommand.getIdCredit())
				.idOffer(acceptOfferCommand.getIdOffer())
				.otp(acceptOfferCommand.getConfirmationLoanOTP())
				.build();
	}

	private ProductOfferRequest buildProductOfferRequest(CreditsV3Entity creditsV3Entity, String idProductOffer) {
		ProductOfferRequest productOfferRequest = new ProductOfferRequest();
		productOfferRequest.setIdClient(creditsV3Entity.getIdClient());
		productOfferRequest.setIdProductOffer(idProductOffer);
		productOfferRequest.setStatus(ProductOfferStatus.CLOSED);
		return productOfferRequest;
	}
	
	private GetSavingAcountTypeResponse buildGetSavingAcountTypeResponse() {
		return GetSavingAcountTypeResponse.builder()
				.idSavingAccount("idSavingAccount")
				.build();
	}
	
	private AcceptOfferCommand buildAcceptOfferCommand() {
		return AcceptOfferCommand.builder()
				.automaticDebitPayments(true)
				.confirmationLoanOTP("1111")
				.credentials(new AdapterCredentials(new HashMap<String, String>()))
				.dayOfPay(15)
				.idClient("idClient")
				.idCredit(UUID.randomUUID().toString())
				.idOffer("idOffer")
				.idProductOffer("idProductOffer")
				.installment(48)
				.loanPurpose("loanPurpose")
				.build();
	}
}
