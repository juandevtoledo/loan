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

import com.lulobank.credits.starter.v3.adapters.in.dto.AddToWaitingRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.v3.usecase.waitinglist.AddToWaitingListUseCase;
import com.lulobank.credits.v3.usecase.waitinglist.command.AddToWaitingListRequest;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;

public class WaitingListHandlerTest {
	
	private WaitingListHandler subject;
	
	@Mock
	private AddToWaitingListUseCase addToWaitingListUseCase;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new WaitingListHandler(addToWaitingListUseCase);
	}
	
	@Test
	public void handlerShouldReturnOk() {
		
		String idClient = "23123123";
		HttpHeaders headers = new HttpHeaders();
		AddToWaitingRequest addToWaitingRequest = buildAddToWaitingRequest();
		AddToWaitingListRequest addToWaitingListRequest = buildAddToWaitingListRequest(idClient, addToWaitingRequest, headers);
		
		when(addToWaitingListUseCase.execute(refEq(addToWaitingListRequest, "auth"))).thenReturn(Either.right(Boolean.TRUE));
		
		ResponseEntity<AdapterResponse> response = subject.addToWaitingList(idClient, addToWaitingRequest, headers);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void handlerShouldReturnNotAcceptable() {
		
		String idClient = "23123123";
		HttpHeaders headers = new HttpHeaders();
		AddToWaitingRequest addToWaitingRequest = buildAddToWaitingRequest();
		AddToWaitingListRequest addToWaitingListRequest = buildAddToWaitingListRequest(idClient, addToWaitingRequest, headers);
		
		UseCaseResponseError error = new UseCaseResponseError("businessCode", "providerCode", "detail");

		
		when(addToWaitingListUseCase.execute(refEq(addToWaitingListRequest, "auth"))).thenReturn(Either.left(error));
		
		ResponseEntity<AdapterResponse> response = subject.addToWaitingList(idClient, addToWaitingRequest, headers);
		assertThat(response.getStatusCode(), is(HttpStatus.NOT_ACCEPTABLE));
	}
	
	private AddToWaitingRequest buildAddToWaitingRequest() {
		AddToWaitingRequest addToWaitingRequest = new AddToWaitingRequest();
		addToWaitingRequest.setIdProductOffer("idProductOffer");
		return addToWaitingRequest;
	}
	
	private AddToWaitingListRequest buildAddToWaitingListRequest(String idClient,
			AddToWaitingRequest addToWaitingRequest, HttpHeaders headers) {
		return AddToWaitingListRequest.builder()
				.idClient(idClient)
				.auth(headers.toSingleValueMap())
				.idProductOffer(addToWaitingRequest.getIdProductOffer())
				.build();
	}

}
