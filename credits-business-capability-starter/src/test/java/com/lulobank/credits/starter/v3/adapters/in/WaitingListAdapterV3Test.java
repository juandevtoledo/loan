package com.lulobank.credits.starter.v3.adapters.in;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.credits.starter.v3.adapters.in.dto.AddToWaitingRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.handler.WaitingListHandler;

public class WaitingListAdapterV3Test {

	private WaitingListAdapterV3 subject;

	@Mock
	private WaitingListHandler waitingListHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new WaitingListAdapterV3(waitingListHandler);
	}

	@Test
	public void controllerShouldReturnOk() {
		AddToWaitingRequest addToWaitingRequest = new AddToWaitingRequest();
		String idClient = "23123123";
		ResponseEntity<AdapterResponse> responseEntity = new ResponseEntity<AdapterResponse>(HttpStatus.OK);
		when(waitingListHandler.addToWaitingList(eq(idClient), eq(addToWaitingRequest), any()))
				.thenReturn(responseEntity);

		ResponseEntity<AdapterResponse> response = subject.addToWaitingList(null, idClient, addToWaitingRequest);

		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
}
