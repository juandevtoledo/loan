package com.lulobank.credits.v3.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResultEventV2Message;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;

import io.vavr.control.Either;
import io.vavr.control.Option;

public class RiskEngineResultValidationsServiceTest {
	
    private static final String CREDIT_APPROVED = "PASS";
	private static final String EVENT_STATUS_COMPLETED = "COMPLETED";
    private static final BigDecimal MIN_LOAN_AMOUNT = new BigDecimal(1000000);
	
	private RiskEngineResultValidationsService riskEngineResultValidationsService;
	
	@Mock
	private CreditsV3Repository creditsV3Repository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        riskEngineResultValidationsService = new RiskEngineResultValidationsService(creditsV3Repository);
    }
    
    @Test
    public void amounIsBiggerShouldReturn() {
    	Either<String, BigDecimal> response = riskEngineResultValidationsService.amountIsBiggerThanMinLoanAmount(MIN_LOAN_AMOUNT);
    	assertTrue(response.isRight());
    }
    
    @Test
    public void amounIsBiggerShouldReturnNon() {
    	Either<String, BigDecimal> response = riskEngineResultValidationsService.amountIsBiggerThanMinLoanAmount(new BigDecimal(900000));
    	assertTrue(response.isLeft());
    }
    
    @Test
    public void isPreApprovedCreditShouldReturn() {
    	Either<String, String> response = riskEngineResultValidationsService.isPreApprovedCredit(CREDIT_APPROVED);
    	assertTrue(response.isRight());
    }
    
    @Test
    public void isPreApprovedCreditShouldReturnNon() {
    	Either<String, String> response = riskEngineResultValidationsService.isPreApprovedCredit("XX");
    	assertTrue(response.isLeft());
    }
    
    @Test
    public void clientDoesntHaveActiveCreditShouldReturn() {
    	RiskEngineResultEventV2Message engineResultEventV2Message = buildRiskEngineResultEventV2Message(EVENT_STATUS_COMPLETED);
    	when(creditsV3Repository.findLoanActiveByIdClient(engineResultEventV2Message.getIdClient())).thenReturn(Option.none());
    	Either<String, String> response = riskEngineResultValidationsService.clientDoesntHaveActiveCredit(engineResultEventV2Message.getIdClient());
    	assertTrue(response.isRight());
    }
    
    @Test
    public void clientDoesntHaveActiveCreditShouldReturnNon() {
    	RiskEngineResultEventV2Message engineResultEventV2Message = buildRiskEngineResultEventV2Message(EVENT_STATUS_COMPLETED);
    	when(creditsV3Repository.findLoanActiveByIdClient(engineResultEventV2Message.getIdClient())).thenReturn(Option.of(new CreditsV3Entity()));
    	Either<String, String> response = riskEngineResultValidationsService.clientDoesntHaveActiveCredit(engineResultEventV2Message.getIdClient());
    	assertTrue(response.isLeft());
    }

    @Test
    public void isEventCompletedShouldReturn() {
    	Either<String, String> response = riskEngineResultValidationsService.isEventCompleted(EVENT_STATUS_COMPLETED);
    	assertTrue(response.isRight());
    }
    
    @Test
    public void isEventCompletedShouldReturnNon() {
    	Either<String, String> response = riskEngineResultValidationsService.isEventCompleted("XX");
    	assertTrue(response.isLeft());
    }

	private RiskEngineResultEventV2Message buildRiskEngineResultEventV2Message(String status) {
		RiskEngineResultEventV2Message riskEngineResultEventV2Message = new RiskEngineResultEventV2Message();
		riskEngineResultEventV2Message.setIdClient("idClient");
		riskEngineResultEventV2Message.setStatus(status);
		return riskEngineResultEventV2Message;
	}
}
