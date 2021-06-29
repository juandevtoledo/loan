package com.lulobank.credits.starter.v3.adapters.out.savingsaccounts;

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

import com.lulobank.credits.starter.v3.adapters.out.savingsaccounts.SavingAccountServiceAdapter;
import com.lulobank.credits.starter.v3.adapters.out.savingsaccounts.dto.SavingAccountType;
import com.lulobank.credits.starter.v3.adapters.out.savingsaccounts.dto.SavingAccountType.Content;
import com.lulobank.credits.v3.port.out.saving.dto.GetSavingAcountTypeResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;

public class SavingAccountServiceAdapterTest {

	private static final String GET_SAVING_RESOURCE = "savingsaccounts/account/client/%s";

	private SavingAccountServiceAdapter subject;

	@Mock
	private RestTemplateClient clientsRestTemplateClient;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new SavingAccountServiceAdapter(clientsRestTemplateClient);
	}

	@Test
	public void getClientInformationShouldReturnRight() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		SavingAccountType getSavingAccountType = buildSavingAccountType();
		String resource = String.format(GET_SAVING_RESOURCE, idClient);
		when(clientsRestTemplateClient.get(resource, auth, SavingAccountType.class))
				.thenReturn(Either.right(new ResponseEntity<>(getSavingAccountType, HttpStatus.ACCEPTED)));
		Either<UseCaseResponseError, GetSavingAcountTypeResponse> response = subject.getSavingAccount(idClient, auth);
		
		assertTrue(response.isRight());
		assertThat(response.get().getIdSavingAccount(), is(getSavingAccountType.getContent().getIdSavingAccount()));
	}
	
	@Test
	public void getClientInformationShouldReturnLeft() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		String resource = String.format(GET_SAVING_RESOURCE, idClient);
		when(clientsRestTemplateClient.get(resource, auth, SavingAccountType.class))
				.thenReturn(Either.left(new HttpError("500", "Unxepected error trying to consume rest client", null)));
		Either<UseCaseResponseError, GetSavingAcountTypeResponse> response = subject.getSavingAccount(idClient, auth);
		
		assertTrue("Not found error", response.isLeft());
	}

	private SavingAccountType buildSavingAccountType() {
		SavingAccountType savingAccountType = new SavingAccountType();
		savingAccountType.setContent(new Content());
		savingAccountType.getContent().setIdSavingAccount("idSavingAccount");
		savingAccountType.getContent().setState("state");
		return savingAccountType;
	}

}
