package com.lulobank.credits.services.features.getloandetail;

import com.lulobank.core.Response;
import com.lulobank.credits.Samples;
import com.lulobank.credits.sdk.dto.loandetails.LoanDetail;
import com.lulobank.credits.v3.dto.LoanConditionsEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.response.GetLoanResponse;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.collection.List;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GetLoanDetailHandlerTest {

    private GetLoanDetailHandler getLoanDetailHandler;
    private FlexibilitySdk flexibilitySdk;
    private CreditsV3Repository creditsV3Repository;

    @Before
    public void setUp() {
        flexibilitySdk = Mockito.mock(FlexibilitySdk.class);
        creditsV3Repository = Mockito.mock(CreditsV3Repository.class);
        getLoanDetailHandler = new GetLoanDetailHandler(flexibilitySdk, creditsV3Repository);
    }

    @Test
    public void handleWhenBalanceIsGreaterThanZero() throws IOException, ProviderException {
        Double amount = 50d;
        List<CreditsV3Entity> creditsEntitiesList = List.of(Samples.creditsV3EntityBuilder());
        Mockito.when(creditsV3Repository.findByidClientAndIdLoanAccountMambuNotNull(Mockito.any())).thenReturn(creditsEntitiesList);

        GetLoanResponse getLoanResponse = getGetLoanResponse(amount);
        Mockito.when(flexibilitySdk.getLoanByLoanAccountId(Mockito.any())).thenReturn(getLoanResponse);

        Response<java.util.List<LoanDetail>> response = getLoanDetailHandler.handle(new GetLoanDetail("idClient"));

        assertThat(response.getContent().size(), is(equalTo(1)));
    }


    @Test
    public void handleWhenBalanceIsLowerThanZero() throws IOException, ProviderException {
        Double amount = -50d;
        List<CreditsV3Entity> creditsEntitiesList = List.of(Samples.creditsV3EntityBuilder());
        Mockito.when(creditsV3Repository.findByidClientAndIdLoanAccountMambuNotNull(Mockito.any())).thenReturn(creditsEntitiesList);

        GetLoanResponse getLoanResponse = getGetLoanResponse(amount);
        Mockito.when(flexibilitySdk.getLoanByLoanAccountId(Mockito.any())).thenReturn(getLoanResponse);

        Response<java.util.List<LoanDetail>> response = getLoanDetailHandler.handle(new GetLoanDetail("idClient"));

        assertThat(response.getContent().size(), is(equalTo(0)));
    }

    @Test
    public void handleWhenFlexibilityFails() throws IOException, ProviderException {
        Double amount = 50d;
        CreditsV3Entity element = Samples.creditsV3EntityBuilder();
        ArrayList<LoanConditionsEntityV3> loanConditionsList = new ArrayList<>();
        loanConditionsList.add(new LoanConditionsEntityV3());
        element.setLoanConditionsList(loanConditionsList);
        List<CreditsV3Entity> creditsEntitiesList = List.of(element);
        Mockito.when(creditsV3Repository.findByidClientAndIdLoanAccountMambuNotNull(Mockito.any())).thenReturn(creditsEntitiesList);

        GetLoanResponse getLoanResponse = getGetLoanResponse(amount);
        Mockito.when(flexibilitySdk.getLoanByLoanAccountId(Mockito.any())).thenThrow(new ProviderException("error", "error"));

        Response<java.util.List<LoanDetail>> response = getLoanDetailHandler.handle(new GetLoanDetail("idClient"));

        assertThat(response.getContent().size(), is(equalTo(0)));
    }

    @NotNull
    private GetLoanResponse getGetLoanResponse(Double amount) {
        GetLoanResponse getLoanResponse = new GetLoanResponse();
        GetLoanResponse.Balance balance = new GetLoanResponse.Balance();
        balance.setAmount(amount);
        getLoanResponse.setBalance(balance);
        getLoanResponse.setPaymentPlanItemApiList(new ArrayList<>());

        getLoanResponse.setInterestRate(23);
        return getLoanResponse;
    }
}