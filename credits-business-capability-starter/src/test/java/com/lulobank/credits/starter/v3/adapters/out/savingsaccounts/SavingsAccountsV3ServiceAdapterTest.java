package com.lulobank.credits.starter.v3.adapters.out.savingsaccounts;

import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.savingsaccounts.sdk.dto.createsavingaccountv3.SavingAccountCreated;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static com.lulobank.credits.starter.utils.Constants.ACCOUNT_ID;
import static com.lulobank.credits.starter.utils.Constants.ID_LOAN;
import static com.lulobank.credits.starter.utils.Samples.clientInformationV3Builder;
import static com.lulobank.credits.starter.utils.Samples.savingsAccountRequestBuilder;
import static com.lulobank.credits.starter.utils.Samples.createSavingAccountCreated;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class SavingsAccountsV3ServiceAdapterTest {

	private SavingsAccountsV3ServiceAdapter subject;

	@Mock
	private RestTemplateClient restTemplateClient;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new SavingsAccountsV3ServiceAdapter(restTemplateClient);
	}

	@Test
	public void create() {
		Either<HttpError, ResponseEntity<SavingAccountCreated>> restTemplateResponse = Either
				.right(new ResponseEntity<SavingAccountCreated>(createSavingAccountCreated(), HttpStatus.ACCEPTED));
		when(restTemplateClient.post(any(), any(), any(), eq(SavingAccountCreated.class))).thenReturn(restTemplateResponse);
		Either<HttpError, SavingsAccountResponse> reponse = subject
				.create(savingsAccountRequestBuilder(clientInformationV3Builder()), new HashMap<>());
		assertTrue("Not found error", reponse.isRight());
		assertThat("Account id is right", reponse.get().getAccountId(), is(ACCOUNT_ID));
		assertThat("Promissory id is right", reponse.get().getIdCbs(), is(ID_LOAN));
	}

	@Test
	public void ServiceException() {
		Either<HttpError, ResponseEntity<SavingAccountCreated>> restTemplateResponse = Either
				.left(new HttpError("500", "Unxepected error trying to consume rest client", null));
		when(restTemplateClient.post(any(), any(), any(), eq(SavingAccountCreated.class))).thenReturn(restTemplateResponse);
		Either<HttpError, SavingsAccountResponse> reponse = subject
				.create(savingsAccountRequestBuilder(clientInformationV3Builder()), new HashMap<>());
		assertTrue("Not found error", reponse.isLeft());
		assertThat("Account id is right", reponse.getLeft().getStatusCode(), is("500"));
	}
}
