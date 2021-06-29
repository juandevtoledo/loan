package com.lulobank.credits.starter.v3.adapters.in.acceptoffer;

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

import com.lulobank.credits.starter.v3.adapters.in.acceptoffer.dto.AcceptOfferRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.handler.AcceptOfferHandler;

public class AcceptOfferAdapterTest {

	private AcceptOfferAdapter acceptOfferAdtapter;

	@Mock
	public AcceptOfferHandler acceptOfferHandler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		acceptOfferAdtapter = new AcceptOfferAdapter(acceptOfferHandler);
	}

	@Test
	public void shouldAcceptOffer() {
		AcceptOfferRequest acceptOfferRequest = new AcceptOfferRequest();
		String idClient = "23123123";
		ResponseEntity<AdapterResponse> responseEntity = new ResponseEntity<AdapterResponse>(HttpStatus.OK);
		when(acceptOfferHandler.acceptOffer(eq(idClient), eq(acceptOfferRequest), any())).thenReturn(responseEntity);
		ResponseEntity<AdapterResponse> response = acceptOfferAdtapter.acceptOffer(null, idClient, acceptOfferRequest);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
}
