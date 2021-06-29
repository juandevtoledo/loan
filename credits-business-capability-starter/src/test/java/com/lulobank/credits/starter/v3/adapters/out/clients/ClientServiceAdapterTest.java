package com.lulobank.credits.starter.v3.adapters.out.clients;

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

import com.lulobank.credits.starter.v3.adapters.out.clients.dto.GetClientInformationResponse;
import com.lulobank.credits.starter.v3.adapters.out.clients.dto.GetClientInformationResponse.Content;
import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;

public class ClientServiceAdapterTest {

	private static final String GET_CLIENT_BY_ID_RESOURCE = "clients/idClient/%s";

	private ClientServiceAdapter subject;

	@Mock
	private RestTemplateClient clientsRestTemplateClient;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new ClientServiceAdapter(clientsRestTemplateClient);
	}

	@Test
	public void getClientInformationShouldReturnRight() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		String documentNumber = "124"; 
		String name = "nombre"; 
		String lastName = "Apellido";
		GetClientInformationResponse clientInformationResponse = buildGetClientInformationResponse(documentNumber, name, lastName);
		String resource = String.format(GET_CLIENT_BY_ID_RESOURCE, idClient);
		when(clientsRestTemplateClient.get(resource, auth, GetClientInformationResponse.class))
				.thenReturn(Either.right(new ResponseEntity<>(clientInformationResponse, HttpStatus.ACCEPTED)));
		Either<UseCaseResponseError, ClientInformationResponse> response = subject.getClientInformation(idClient, auth);
		
		assertTrue("Not found error", response.isRight());
		assertThat("Account id is right", response.get().getDocumentNumber(), is(documentNumber));
		assertThat("Promissory id is right", response.get().getName(), is(name));
		assertThat("Promissory id is right", response.get().getLastName(), is(lastName));
	}
	
	@Test
	public void getClientInformationShouldReturnLeft() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		String resource = String.format(GET_CLIENT_BY_ID_RESOURCE, idClient);
		when(clientsRestTemplateClient.get(resource, auth, GetClientInformationResponse.class))
				.thenReturn(Either.left(new HttpError("500", "Unxepected error trying to consume rest client", null)));
		Either<UseCaseResponseError, ClientInformationResponse> response = subject.getClientInformation(idClient, auth);
		
		assertTrue("Not found error", response.isLeft());
	}

	private GetClientInformationResponse buildGetClientInformationResponse(String documentNumber, String name, String lastName) {
		GetClientInformationResponse clientInformationResponse = new GetClientInformationResponse();
		clientInformationResponse.setContent(new Content());
		clientInformationResponse.getContent().setIdCard(documentNumber);
		clientInformationResponse.getContent().setName(name);
		clientInformationResponse.getContent().setLastName(lastName);
		return clientInformationResponse;
	}

}
