package com.lulobank.credits.sdk.operations.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.credits.sdk.dto.initialofferv2.ClientInformation;
import com.lulobank.credits.sdk.dto.initialofferv2.DocumentId;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.sdk.dto.initialofferv2.Phone;
import com.lulobank.credits.sdk.dto.initialofferv2.RiskEngineAnalysis;
import com.lulobank.credits.sdk.exceptions.InitialOffersException;
import com.lulobank.credits.sdk.operations.InitialOffersOperations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import retrofit2.Call;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.lulobank.credits.sdk.operations.impl.Constants.AMOUNT;
import static com.lulobank.credits.sdk.operations.impl.Constants.AMOUNT_INSTALMENT;
import static com.lulobank.credits.sdk.operations.impl.Constants.GENDER;
import static com.lulobank.credits.sdk.operations.impl.Constants.ID_CARD;
import static com.lulobank.credits.sdk.operations.impl.Constants.INTEREST_RATE;
import static com.lulobank.credits.sdk.operations.impl.Constants.LAST_NAME;
import static com.lulobank.credits.sdk.operations.impl.Constants.NAME;
import static com.lulobank.credits.sdk.operations.impl.Constants.PHONE_NUMBER;
import static com.lulobank.credits.sdk.operations.impl.Constants.PREFIX;
import static com.lulobank.credits.sdk.operations.impl.Constants.PURPOSE;
import static com.lulobank.credits.sdk.operations.impl.Constants.TYPE;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

public class InitialOffersOperationsV2Test {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8981);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private InitialOffersOperations testClases;

    private String ID_CLIENT= UUID.randomUUID().toString();
    private ObjectMapper objectMapper;
    @Before
    public void setup() {
        objectMapper=new ObjectMapper();
        MockitoAnnotations.initMocks(this);
        testClases = new RetrofitInitialOffersOperations("http://localhost:8981/");

    }

    @Test
    public void initials_offer_ok() throws JsonProcessingException {

        String stubUrl = "/credits/products/v2/loan/client/".concat(ID_CLIENT).concat("/initial-offer");
        GetOfferToClient getOfferToClient = setGetOfferClient();

        StubMapping stubMapping = wireMockRule.stubFor(post(urlEqualTo(stubUrl)).
                willReturn(aResponse().withStatus(CREATED.value())
                        .withHeader("Content-Type", "application/json")));

        Map<String, String> headers = new HashMap<>();
        boolean response = testClases.initialOffers(headers,
                getOfferToClient,ID_CLIENT);
        assertTrue("Response is true",response);

    }

    @Test
    public void initials_offer_bad_request() throws JsonProcessingException {

        String stubUrl = "/credits/products/v2/loan/client/".concat(ID_CLIENT).concat("/initial-offer");
        GetOfferToClient getOfferToClient = setGetOfferClient();
        getOfferToClient.setClientInformation(new ClientInformation());

        StubMapping stubMapping = wireMockRule.stubFor(post(urlEqualTo(stubUrl)).
                willReturn(aResponse().withStatus(BAD_REQUEST.value())
                        .withHeader("Content-Type", "application/json")));

        exceptionRule.expect(InitialOffersException.class);
        exceptionRule.expectMessage("Error try to generate Offers");

        Map<String, String> headers = new HashMap<>();
        boolean response = testClases.initialOffers(headers,
                getOfferToClient,ID_CLIENT);


    }

    @Test
    public void initials_offer_error_service() throws JsonProcessingException {

        String stubUrl = "/credits/products/v2/loan/client/".concat(ID_CLIENT).concat("/initial-offer");
        GetOfferToClient getOfferToClient = setGetOfferClient();
        getOfferToClient.setClientInformation(new ClientInformation());
        ValidationResult validationResult=new ValidationResult("Falure","405");
        objectMapper.writeValueAsString(validationResult);
        StubMapping stubMapping = wireMockRule.stubFor(post(urlEqualTo(stubUrl)).
                willReturn(aResponse().withStatus(502)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(validationResult))));

        exceptionRule.expect(InitialOffersException.class);
        exceptionRule.expectMessage("Error try to generate Offers");
        Map<String, String> headers = new HashMap<>();
        boolean response = testClases.initialOffers(headers,
                getOfferToClient,ID_CLIENT);


    }


    private static GetOfferToClient setGetOfferClient() {
        DocumentId documentId=Sample.documentIdBuilder(ID_CARD, TYPE);
        Phone phone=Sample.phoneBuilder(PHONE_NUMBER, PREFIX);
        ClientInformation clientInformation=Sample.clientInformationBuilder(documentId,phone, NAME, LAST_NAME, GENDER);
        RiskEngineAnalysis riskEngineAnalysisBuilder=Sample.riskEngineAnalysisBuilder(AMOUNT, AMOUNT_INSTALMENT, INTEREST_RATE);
        return Sample.getOfferToClientBuilder(AMOUNT, PURPOSE,clientInformation,riskEngineAnalysisBuilder);
    }
}

