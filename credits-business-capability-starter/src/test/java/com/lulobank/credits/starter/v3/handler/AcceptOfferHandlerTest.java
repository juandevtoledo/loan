package com.lulobank.credits.starter.v3.handler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.credits.starter.v3.adapters.in.acceptoffer.dto.AcceptOfferRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.v3.usecase.acceptoffer.AcceptOfferUseCase;
import com.lulobank.credits.v3.usecase.acceptoffer.command.AcceptOfferCommand;
import com.lulobank.credits.v3.usecase.acceptoffer.command.AcceptOfferUseCaseResponse;
import com.lulobank.credits.v3.vo.AdapterCredentials;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;

public class AcceptOfferHandlerTest {
	
	private AcceptOfferHandler acceptOfferHandler;
	
	@Mock
	private AcceptOfferUseCase acceptOfferUseCase;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		acceptOfferHandler = new AcceptOfferHandler(acceptOfferUseCase);
	}
	
	@Test
	public void handlerShouldReturnCreated() {
		
		String idClient = "23123123";
		HttpHeaders headers = new HttpHeaders();
		AcceptOfferRequest acceptOfferRequest = buildAcceptOfferRequest();
		AcceptOfferCommand acceptOfferCommand = buildAcceptOfferCommand(idClient, acceptOfferRequest, headers);
		AcceptOfferUseCaseResponse acceptOfferUseCaseResponse = new AcceptOfferUseCaseResponse(true);
		
		when(acceptOfferUseCase.execute(refEq(acceptOfferCommand, "credentials"))).thenReturn(Either.right(acceptOfferUseCaseResponse));
		
		ResponseEntity<AdapterResponse> response = acceptOfferHandler.acceptOffer(idClient, acceptOfferRequest, headers);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
	}
	
	@Test
	public void handlerShouldReturnNotAcceptable() {
		
		String idClient = "23123123";
		HttpHeaders headers = new HttpHeaders();
		AcceptOfferRequest acceptOfferRequest = buildAcceptOfferRequest();
		AcceptOfferCommand acceptOfferCommand = buildAcceptOfferCommand(idClient, acceptOfferRequest, headers);
		UseCaseResponseError error = new UseCaseResponseError("CRE_116", "providerCode", "detail");
		
		when(acceptOfferUseCase.execute(refEq(acceptOfferCommand, "credentials"))).thenReturn(Either.left(error));
		
		ResponseEntity<AdapterResponse> response = acceptOfferHandler.acceptOffer(idClient, acceptOfferRequest, headers);
		assertThat(response.getStatusCode(), is(HttpStatus.NOT_ACCEPTABLE));
	}
	
	
	private AcceptOfferRequest buildAcceptOfferRequest() {
		AcceptOfferRequest acceptOfferRequest = new AcceptOfferRequest();
		acceptOfferRequest.setAutomaticDebitPayments(true);
		acceptOfferRequest.setConfirmationLoanOTP("1111");
		acceptOfferRequest.setDayOfPay(15);
		acceptOfferRequest.setIdCredit("idCredit");
		acceptOfferRequest.setIdOffer("idOffer");
		acceptOfferRequest.setIdProductOffer("idProductOffer");
		acceptOfferRequest.setInstallment(24);
		acceptOfferRequest.setLoanPurpose("loanPurpose");
		return acceptOfferRequest;
	}

	private AcceptOfferCommand buildAcceptOfferCommand(String idClient, AcceptOfferRequest acceptOfferRequest,
			HttpHeaders headers) {
		return AcceptOfferCommand.builder()
				.idClient(idClient)
				.idCredit(acceptOfferRequest.getIdCredit())
				.idOffer(acceptOfferRequest.getIdOffer())
				.idProductOffer(acceptOfferRequest.getIdProductOffer())
				.automaticDebitPayments(acceptOfferRequest.isAutomaticDebitPayments())
				.confirmationLoanOTP(acceptOfferRequest.getConfirmationLoanOTP())
				.credentials(new AdapterCredentials(headers.toSingleValueMap()))
				.dayOfPay(acceptOfferRequest.getDayOfPay())
				.installment(acceptOfferRequest.getInstallment())
				.loanPurpose(acceptOfferRequest.getLoanPurpose())
				.build();
	}

}
