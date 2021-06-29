package com.lulobank.credits.sdk.operations.impl;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.credits.sdk.dto.ResponsePendingValidations;
import com.lulobank.utils.exception.ServiceException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class PendingValidationsOperationsTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8981);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void validateEmailSuccess() {
        PendingValidationsOperations pendingValidationsOperations = new PendingValidationsOperations("http://localhost:8981/");

        wireMockRule.stubFor(get(urlMatching("/clients/pendingvalidations/MNW1234"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("")
                        .withHeader("Content-Type", "application/json")));

        Map<String, String> headers = new HashMap<>();

        exceptionRule.expect(ServiceException.class);
        exceptionRule.expectMessage("");;

        pendingValidationsOperations.getPendingValidations(headers, "MNW1234");

    }
}
