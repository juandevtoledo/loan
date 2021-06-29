package com.lulobank.credits.v3.port.in.approvedriskengine;

import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResponseMessage.ClientInformation;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResponseMessage.Document;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResponseMessage.RiskEngineAnalysis;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class RiskEngineResponseUseCaseTest {

	private RiskEngineResponseUseCase subject;

	@Mock
	private CreditsV3Repository creditsV3Repository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new RiskEngineResponseUseCase(creditsV3Repository);
	}
	
	@Test
	public void shouldExecuteUseCaseSuccess() {
		RiskEngineResponseMessage riskEngineResponseMessage = buildRiskEngineResponseMessage();
		when(creditsV3Repository.save(isA(CreditsV3Entity.class))).thenReturn(Try.of(() -> new CreditsV3Entity()));
		Try<Void> response = subject.execute(riskEngineResponseMessage);
		assertEquals(true, response.isSuccess());
	}
	
	@Test
	public void shouldExecuteUseCaseFaild() {
		RiskEngineResponseMessage riskEngineResponseMessage = buildRiskEngineResponseMessage();
		when(creditsV3Repository.save(isA(CreditsV3Entity.class))).thenReturn(Try.failure(new RuntimeException()));
		Try<Void> response = subject.execute(riskEngineResponseMessage);
		assertEquals(true, response.isFailure());
	}

	private RiskEngineResponseMessage buildRiskEngineResponseMessage() {
		RiskEngineResponseMessage riskEngineResponseMessage = new RiskEngineResponseMessage();
		riskEngineResponseMessage.setIdProductOffer("idProductOffer");
		riskEngineResponseMessage.setIdClient("idClient");
		riskEngineResponseMessage.setClientInformation(buildClientInformation());
		riskEngineResponseMessage.setRiskEngineAnalysis(buildRistEngineAnalysis());
		return riskEngineResponseMessage;
	}

	private RiskEngineAnalysis buildRistEngineAnalysis() {
		RiskEngineAnalysis riskEngineAnalysis = new RiskEngineAnalysis();
		riskEngineAnalysis.setAmount(10000D);
		riskEngineAnalysis.setInstallments(12);
		riskEngineAnalysis.setInterestRate(16.5F);
		riskEngineAnalysis.setMaxAmountInstallment(200000D);
		return riskEngineAnalysis;
	}

	private ClientInformation buildClientInformation() {
		ClientInformation clientInformation = new ClientInformation();
		Document document = new Document();
		document.setId("id");
		document.setType("type");
		clientInformation.setDocumentId(document);
		clientInformation.setEmail("email");
		clientInformation.setGender("gender");
		clientInformation.setLastName("lastName");
		clientInformation.setMiddleName("middleName");
		return clientInformation;
	}
}
