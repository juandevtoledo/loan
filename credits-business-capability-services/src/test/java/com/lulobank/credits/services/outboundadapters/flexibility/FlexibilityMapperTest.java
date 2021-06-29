package com.lulobank.credits.services.outboundadapters.flexibility;

import com.lulobank.credits.sdk.dto.loandetails.LoanDetail;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.dto.LoanConditionsEntityV3;
import com.lulobank.credits.v3.dto.RiskEngineAnalysisV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import flexibility.client.models.request.GetLoanMovementsRequest;
import flexibility.client.models.response.GetLoanResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class FlexibilityMapperTest {

    @Test
    public void getCreditProductsFromGetLoanResponse() {
        GetLoanResponse getLoanResponse = new GetLoanResponse();
        GetLoanResponse.Balance balance = new GetLoanResponse.Balance();
        balance.setAmount(50d);
        getLoanResponse.setBalance(balance);
        getLoanResponse.setPaymentPlanItemApiList(new ArrayList<>());
        getLoanResponse.setInterestRate(23);
        CreditsV3Entity creditsEntity = new CreditsV3Entity();
        InitialOfferV3 initialOffer = new InitialOfferV3();
        initialOffer.setInterestRate(34f);
        RiskEngineAnalysisV3 riskEngine = new RiskEngineAnalysisV3();
        riskEngine.setInterestRate(45f);
        initialOffer.setRiskEngineAnalysis(riskEngine);
        creditsEntity.setInitialOffer(initialOffer);
        creditsEntity.setIdCredit(UUID.randomUUID());
        ArrayList<LoanConditionsEntityV3> loanConditionsList = new ArrayList<>();
        loanConditionsList.add(new LoanConditionsEntityV3());
        creditsEntity.setLoanConditionsList(loanConditionsList);
        LoanDetail creditProductsFromGetLoanResponse = FlexibilityMapper.getCreditProductsFromGetLoanResponse(getLoanResponse, creditsEntity);
        assertThat(creditProductsFromGetLoanResponse, is(notNullValue()));
    }


    @Test
    public void getCreditProductsFromGetLoanResponse2() {
        GetLoanResponse getLoanResponse = new GetLoanResponse();
        GetLoanResponse.Balance balance = new GetLoanResponse.Balance();
        balance.setAmount(50d);
        getLoanResponse.setBalance(balance);
        getLoanResponse.setPaymentPlanItemApiList(new ArrayList<>());
        getLoanResponse.setInterestRate(23);
        CreditsV3Entity creditsEntity = new CreditsV3Entity();
        InitialOfferV3 initialOffer = new InitialOfferV3();
        initialOffer.setInterestRate(34f);
        RiskEngineAnalysisV3 riskEngine = new RiskEngineAnalysisV3();
        riskEngine.setInterestRate(45f);
        initialOffer.setRiskEngineAnalysis(riskEngine);
        creditsEntity.setIdCredit(UUID.randomUUID());
        ArrayList<LoanConditionsEntityV3> loanConditionsList = new ArrayList<>();
        LoanConditionsEntityV3 e = new LoanConditionsEntityV3();
        e.setInterestRate(45f);
        loanConditionsList.add(e);
        creditsEntity.setLoanConditionsList(loanConditionsList);
        LoanDetail creditProductsFromGetLoanResponse = FlexibilityMapper.getCreditProductsFromGetLoanResponse(getLoanResponse, creditsEntity);
        assertThat(creditProductsFromGetLoanResponse, is(notNullValue()));
    }

    @Test
    public void getLoanMovementsRequest() {
        CreditsEntity creditsEntity = new CreditsEntity();
        creditsEntity.setIdCredit(UUID.randomUUID());
        GetLoanMovementsRequest loanMovementsRequest = FlexibilityMapper.getLoanMovementsRequest(creditsEntity);
        assertThat(loanMovementsRequest, is(notNullValue()));
    }
}