package com.lulobank.credits.v3.port.in.approvedriskengine;

import com.lulobank.credits.services.exceptions.CreditNotFoundException;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResultEventV2Message.RiskResult;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferNotificationService;
import com.lulobank.credits.v3.service.PreApprovedClientOfferService;
import com.lulobank.credits.v3.service.RiskEngineResultValidationsService;

import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

public class RiskEngineResultEventV2UseCaseTest {

    private static final BigDecimal VALID_AMOUNT = BigDecimal.valueOf(1100000);
    private static final BigDecimal INVALID_AMOUNT = BigDecimal.valueOf(999999);
    private static final BigDecimal MILLION_AMOUNT = BigDecimal.valueOf(1000000);
	private static final String EVENT_STATUS_COMPLETED = "COMPLETED";
    private static final String EVENT_STATUS_NON_COMPLETED = "NONCOMPLETED";
    private static final String CREDIT_APPROVED = "PASS";

	private RiskEngineResultEventV2UseCase riskResultUseCase;

    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private ProductOfferNotificationService productOfferNotificationService;
    @Mock
    private RiskEngineResultValidationsService riskEngineResultValidationsService;
    @Mock
    private PreApprovedClientOfferService clientOfferService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        riskResultUseCase = new RiskEngineResultEventV2UseCase(creditsV3Repository, productOfferNotificationService, riskEngineResultValidationsService,
        		clientOfferService);
    }

    @Test
    public void thereIsACreditOfferOK() {
        RiskEngineResultEventV2Message riskEngineResultEventV2Message = buildRiskEngineResultEventV2Message(EVENT_STATUS_COMPLETED, CREDIT_APPROVED, VALID_AMOUNT);
        RiskResult riskResult = riskEngineResultEventV2Message.getResults().stream().findFirst().get();
        when(riskEngineResultValidationsService.isEventCompleted(riskEngineResultEventV2Message.getStatus())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.clientDoesntHaveActiveCredit(riskEngineResultEventV2Message.getIdClient())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.amountIsBiggerThanMinLoanAmount(riskResult.getLoanAmount())).thenReturn(Either.right(riskResult.getLoanAmount()));
        when(riskEngineResultValidationsService.isPreApprovedCredit(riskResult.getApproved())).thenReturn(Either.right(""));
        when(clientOfferService.closePreApprovedClientOffers(riskEngineResultEventV2Message.getIdClient())).thenReturn(Try.of(() -> ""));
        when(creditsV3Repository.save(isA(CreditsV3Entity.class))).thenReturn(Try.of(CreditsV3Entity::new));
        when(productOfferNotificationService.createProductOffer(any())).thenReturn(Try.run(() -> System.out.println("")));
        
        Try<Void> response = riskResultUseCase.execute(riskEngineResultEventV2Message);
        assertTrue(response.isSuccess());
        verify(creditsV3Repository).save(any());
    }


    @Test
    public void thereIsACreditOfferFailed() {
        RiskEngineResultEventV2Message riskEngineResultEventV2Message = buildRiskEngineResultEventV2Message(EVENT_STATUS_COMPLETED, CREDIT_APPROVED, VALID_AMOUNT);
        RiskResult riskResult = riskEngineResultEventV2Message.getResults().stream().findFirst().get();
        when(riskEngineResultValidationsService.isEventCompleted(riskEngineResultEventV2Message.getStatus())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.clientDoesntHaveActiveCredit(riskEngineResultEventV2Message.getIdClient())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.amountIsBiggerThanMinLoanAmount(riskResult.getLoanAmount())).thenReturn(Either.right(riskResult.getLoanAmount()));
        when(riskEngineResultValidationsService.isPreApprovedCredit(riskResult.getApproved())).thenReturn(Either.right(""));
        when(clientOfferService.closePreApprovedClientOffers(riskEngineResultEventV2Message.getIdClient())).thenReturn(Try.of(() -> ""));
        when(creditsV3Repository.save(isA(CreditsV3Entity.class))).thenReturn(Try.failure(new CreditNotFoundException()));
        Try<Void> response = riskResultUseCase.execute(riskEngineResultEventV2Message);
        assertTrue(response.isSuccess());
        verify(creditsV3Repository, times(2)).save(any());
    }

    @Test
    public void eventIsNonCompleted() {
        RiskEngineResultEventV2Message riskEngineResultEventV2Message = buildRiskEngineResultEventV2Message(EVENT_STATUS_NON_COMPLETED, CREDIT_APPROVED, VALID_AMOUNT);
        when(riskEngineResultValidationsService.isEventCompleted(riskEngineResultEventV2Message.getStatus())).thenReturn(Either.left(""));
        Try<Void> response = riskResultUseCase.execute(riskEngineResultEventV2Message);
        assertTrue(response.isSuccess());
        verify(creditsV3Repository).save(any());
    }
    
    @Test
    public void creditIsNotApproved() {
        RiskEngineResultEventV2Message riskEngineResultEventV2Message = buildRiskEngineResultEventV2Message(EVENT_STATUS_COMPLETED, "DENY", VALID_AMOUNT);
        RiskResult riskResult = riskEngineResultEventV2Message.getResults().stream().findFirst().get();
        when(riskEngineResultValidationsService.isEventCompleted(riskEngineResultEventV2Message.getStatus())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.clientDoesntHaveActiveCredit(riskEngineResultEventV2Message.getIdClient())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.amountIsBiggerThanMinLoanAmount(riskResult.getLoanAmount())).thenReturn(Either.right(riskResult.getLoanAmount()));
        when(riskEngineResultValidationsService.isPreApprovedCredit(riskResult.getApproved())).thenReturn(Either.left(""));
        Try<Void> response = riskResultUseCase.execute(riskEngineResultEventV2Message);
        assertTrue(response.isSuccess());
        verify(creditsV3Repository).save(any());
    }
    
    @Test
    public void creditValueIsLessThanMin() {
        RiskEngineResultEventV2Message riskEngineResultEventV2Message = buildRiskEngineResultEventV2Message(EVENT_STATUS_COMPLETED, CREDIT_APPROVED, INVALID_AMOUNT);
        RiskResult riskResult = riskEngineResultEventV2Message.getResults().stream().findFirst().get();
        when(riskEngineResultValidationsService.isEventCompleted(riskEngineResultEventV2Message.getStatus())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.clientDoesntHaveActiveCredit(riskEngineResultEventV2Message.getIdClient())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.amountIsBiggerThanMinLoanAmount(riskResult.getLoanAmount())).thenReturn(Either.left(""));
        when(riskEngineResultValidationsService.isPreApprovedCredit(riskResult.getApproved())).thenReturn(Either.right(""));
        Try<Void> response = riskResultUseCase.execute(riskEngineResultEventV2Message);
        assertTrue(response.isSuccess());
        verify(creditsV3Repository).save(any());
    }
    
    @Test
    public void creditValueIsEqualsThanOneMillion() {
        RiskEngineResultEventV2Message riskEngineResultEventV2Message = buildRiskEngineResultEventV2Message(EVENT_STATUS_COMPLETED, CREDIT_APPROVED, MILLION_AMOUNT);
        RiskResult riskResult = riskEngineResultEventV2Message.getResults().stream().findFirst().get();
        when(riskEngineResultValidationsService.isEventCompleted(riskEngineResultEventV2Message.getStatus())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.clientDoesntHaveActiveCredit(riskEngineResultEventV2Message.getIdClient())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.amountIsBiggerThanMinLoanAmount(riskResult.getLoanAmount())).thenReturn(Either.left(""));
        when(riskEngineResultValidationsService.isPreApprovedCredit(riskResult.getApproved())).thenReturn(Either.right(""));
        Try<Void> response = riskResultUseCase.execute(riskEngineResultEventV2Message);
        assertTrue(response.isSuccess());
        verify(creditsV3Repository).save(any());
    }
    
    @Test
    public void clientHasAnotherLoan() {
        RiskEngineResultEventV2Message riskEngineResultEventV2Message = buildRiskEngineResultEventV2Message(EVENT_STATUS_COMPLETED, CREDIT_APPROVED, VALID_AMOUNT);
        when(riskEngineResultValidationsService.isEventCompleted(riskEngineResultEventV2Message.getStatus())).thenReturn(Either.right(""));
        when(riskEngineResultValidationsService.clientDoesntHaveActiveCredit(riskEngineResultEventV2Message.getIdClient())).thenReturn(Either.left(""));
        Try<Void> response = riskResultUseCase.execute(riskEngineResultEventV2Message);
        assertTrue(response.isSuccess());
        verify(creditsV3Repository).save(any());
    }

    private RiskEngineResultEventV2Message buildRiskEngineResultEventV2Message(String status, String approved, BigDecimal loanAmount) {
        RiskEngineResultEventV2Message riskEngineResultEventV2Message = new RiskEngineResultEventV2Message();
        riskEngineResultEventV2Message.setIdClient("65cf89f3-3a2c-856c-a7ac-7a9e7f419cd1");
        riskEngineResultEventV2Message.setStatus(status);
        riskEngineResultEventV2Message.setResults(buildResults(approved, loanAmount));
        return riskEngineResultEventV2Message;
    }

    private List<RiskEngineResultEventV2Message.RiskResult> buildResults(String approved, BigDecimal loanAmount) {
        List<RiskEngineResultEventV2Message.RiskResult> resultList = new ArrayList<>();

        RiskEngineResultEventV2Message.RiskResult result = new RiskEngineResultEventV2Message.RiskResult();
        result.setType("v1");
        result.setSchedule(buildSchedule());
        result.setMaxAmountInstallment(BigDecimal.valueOf(81341.0));
        result.setMaxTotalAmount(BigDecimal.valueOf(3000000));
        result.setLoanAmount(loanAmount);
        result.setApproved(approved);
        result.setDescription("description");
        result.setScore(0.014688234845045433);

        resultList.add(result);
        return resultList;
    }

    private List<RiskEngineResultEventV2Message.Schedule> buildSchedule() {
        List<RiskEngineResultEventV2Message.Schedule> schedule = new ArrayList<>();

        RiskEngineResultEventV2Message.Schedule schedule1 = new RiskEngineResultEventV2Message.Schedule();
        schedule1.setInstallment(12);
        schedule1.setInterestRateEA(BigDecimal.valueOf(16.55f));
        schedule1.setInterestRateNA(BigDecimal.valueOf(16.55f));
        schedule1.setInterestRatePM(BigDecimal.valueOf(16.55f));

        RiskEngineResultEventV2Message.Schedule schedule2 = new RiskEngineResultEventV2Message.Schedule();
        schedule2.setInstallment(25);
        schedule2.setInterestRateEA(BigDecimal.valueOf(16.3f));
        schedule2.setInterestRateNA(BigDecimal.valueOf(16.3f));
        schedule2.setInterestRatePM(BigDecimal.valueOf(16.3f));

        RiskEngineResultEventV2Message.Schedule schedule3 = new RiskEngineResultEventV2Message.Schedule();
        schedule2.setInstallment(36);
        schedule2.setInterestRateEA(BigDecimal.valueOf(15f));
        schedule2.setInterestRateNA(BigDecimal.valueOf(15f));
        schedule2.setInterestRatePM(BigDecimal.valueOf(15f));

        RiskEngineResultEventV2Message.Schedule schedule4 = new RiskEngineResultEventV2Message.Schedule();
        schedule2.setInstallment(48);
        schedule2.setInterestRateEA(BigDecimal.valueOf(14.82f));
        schedule2.setInterestRateNA(BigDecimal.valueOf(14.82f));
        schedule2.setInterestRatePM(BigDecimal.valueOf(14.82f));

        schedule.add(schedule1);
        schedule.add(schedule2);
        schedule.add(schedule3);
        schedule.add(schedule4);

        return schedule;
    }
}