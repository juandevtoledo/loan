package com.lulobank.credits.starter.v3.adapters.out.sqs.riskengine;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.credits.starter.v3.adapters.out.sqs.riskengine.RiskEngineNotificationServiceSqsAdapter;
import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import io.vavr.control.Try;

public class RiskEngineNotificationServiceSqsAdapterTest {

	private RiskEngineNotificationServiceSqsAdapter subject;
	
	@Mock
	private SqsBraveTemplate sqsBraveTemplate;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new RiskEngineNotificationServiceSqsAdapter(sqsBraveTemplate, "clientsQueue");
	}
	
	@Test
	public void ShouldNotifyRiskEngine() {
		
		ClientInformationResponse clientInformationResponse = buildClientInformationResponse();
		Try<Void> response = subject.sendRiskEngineNotification(clientInformationResponse);
		
		assertTrue(response.isSuccess());
	}

	private ClientInformationResponse buildClientInformationResponse() {
		
		return ClientInformationResponse.builder()
			.birthDate("1999-09-19")
			.idClient("idClient")
			.documentNumber("documentNumber")
			.documentType("C")
			.email("email")
			.expeditionDate("expeditionDate")
			.gender("gender")
			.name("name")
			.lastName("lastName")
			.build();
	}
}
