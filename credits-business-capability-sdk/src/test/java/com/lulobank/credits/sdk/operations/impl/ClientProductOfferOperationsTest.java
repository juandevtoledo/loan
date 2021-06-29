package com.lulobank.credits.sdk.operations.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.lulobank.core.Response;
import com.lulobank.credits.sdk.dto.CreditResult;
import com.lulobank.credits.sdk.dto.CreditSuccessResult;
import com.lulobank.credits.sdk.dto.acceptoffer.Accepted;
import com.lulobank.credits.sdk.dto.acceptoffer.CreditWithOffer;
import com.lulobank.credits.sdk.operations.IClientProductOfferOperations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

public class ClientProductOfferOperationsTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8981);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private static final String ID_CREDIT = "ebc0bb5e-244e-4cfb-801e-0e15697a83cb";
    private static final String ID_OFFER = "e07ff0d1-5000-4a6a-8281-db25eedf6721";
    private IClientProductOfferOperations clientProductOfferOperations;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        clientProductOfferOperations = new ClientProductOfferOperations("http://localhost:8981/");
    }

    @Test
    public void accept_offer_ok() throws JsonProcessingException {

        String stubUrl = "/credits/products/offer/accept";
        CreditWithOffer creditWithOffer = getCreditWithOffer(ID_CREDIT, ID_OFFER);
        Accepted accepted = new Accepted(ID_CREDIT);
        CreditSuccessResult<Accepted> creditSuccessResultRequest = new CreditSuccessResult<>(accepted);
        ObjectMapper mapper = new ObjectMapper();

        String jsonStr = mapper.writeValueAsString(creditSuccessResultRequest);

        StubMapping stubMapping = wireMockRule.stubFor(post(urlEqualTo(stubUrl)).
                willReturn(aResponse().withStatus(200)
                        .withBody(jsonStr)
                        .withHeader("Content-Type", "application/json")));

        Map<String, String> headers = new HashMap<>();

        ResponseEntity<CreditSuccessResult<Accepted>> response = clientProductOfferOperations.acceptOffer(headers,
                creditWithOffer);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(ID_CREDIT, response.getBody().getContent().getIdCredit());

    }

    @Test
    public void should_return_service_exception_since_request_is_invalid() throws JsonProcessingException {

        String stubUrl = "/credits/products/offer/accept";
        CreditWithOffer creditWithOffer = getCreditWithOffer(null, null);  StubMapping stubMapping = wireMockRule.stubFor(post(urlEqualTo(stubUrl)).
                willReturn(aResponse().withStatus(400)
                        .withHeader("Content-Type", "application/json")));



        exceptionRule.expect(com.lulobank.utils.exception.ServiceException.class);

        Map<String, String> headers = new HashMap<>();
        ResponseEntity<CreditSuccessResult<Accepted>> response = clientProductOfferOperations.acceptOffer(headers,
                creditWithOffer);
        assertEquals("IS Bad Request", HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    private CreditWithOffer getCreditWithOffer(String idCredit, String idOffer) {
        CreditWithOffer creditWithOffer = new CreditWithOffer();
        creditWithOffer.setIdCredit(idCredit);
        creditWithOffer.setIdOffer(idOffer);
        return creditWithOffer;
    }
}
