package com.lulobank.credits.starter.v3.adapters.out.pep;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.credits.starter.v3.adapters.out.pep.dto.GetPepServiceResponse;
import com.lulobank.credits.v3.port.out.pep.dto.GetPepResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;

public class PepServiceAdapterTest {

	private static final String CLIENTS_PEP_RESOURCE = "clients/api/v3/client/%s/pep";

	private PepServiceAdapter subject;

	@Mock
	private RestTemplateClient clientsRestTemplateClient;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new PepServiceAdapter(clientsRestTemplateClient);
	}

	@Test
	public void getClientInformationShouldReturnRight() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		GetPepServiceResponse getPepServiceResponse = buildGetPepServiceResponse();
		String resource = String.format(CLIENTS_PEP_RESOURCE, idClient);
		when(clientsRestTemplateClient.get(resource, auth, GetPepServiceResponse.class))
				.thenReturn(Either.right(new ResponseEntity<>(getPepServiceResponse, HttpStatus.ACCEPTED)));
		Either<UseCaseResponseError, GetPepResponse> response = subject.getPep(idClient, auth);
		
		assertTrue(response.isRight());
		assertThat(response.get().getPep(), is(getPepServiceResponse.getPep()));
	}
	
	@Test
	public void getClientInformationShouldReturnLeft() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		String resource = String.format(CLIENTS_PEP_RESOURCE, idClient);
		when(clientsRestTemplateClient.get(resource, auth, GetPepServiceResponse.class))
				.thenReturn(Either.left(new HttpError("500", "Unxepected error trying to consume rest client", null)));
		Either<UseCaseResponseError, GetPepResponse> response = subject.getPep(idClient, auth);
		
		assertTrue("Not found error", response.isLeft());
	}

	private GetPepServiceResponse buildGetPepServiceResponse() {
		GetPepServiceResponse getPepServiceResponse = new GetPepServiceResponse();
		getPepServiceResponse.setPep("-1");
		return getPepServiceResponse;
	}

}
