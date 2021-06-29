package com.lulobank.credits.sdk.operations.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanRequestV3;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanResponseV3;
import com.lulobank.credits.sdk.operations.PaymentPlansOperation;
import io.vavr.control.Either;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Deprecated
public class RetrofitPaymentPlansOperationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8981);
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private PaymentPlanRequestV3 paymentPlanRequestV3;
    private PaymentPlanResponseV3 paymentPlanResponseV3;
    private String idClient;
    private String stubUrl;

    @Before
    public void setup() throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        idClient = UUID.randomUUID().toString();
        paymentPlanRequestV3 = new PaymentPlanRequestV3();
        paymentPlanRequestV3.setIdClient(idClient);
        paymentPlanRequestV3.setIdOffer(UUID.randomUUID().toString());
        paymentPlanRequestV3.setIdCredit(UUID.randomUUID().toString());
        paymentPlanRequestV3.setDayOfPay(15);
        paymentPlanResponseV3 = Sample.paymentPlanBuilder();
        this.stubUrl = "/credits/v3/client/".concat(idClient).concat("/payment-plan");
    }


    @Test
    public void sdkShouldReturnPaymentPlans() throws IOException {
        PaymentPlansOperation paymentPlansOperation = getPaymentPlanOperation("http://localhost:8981");
        String responseBody = objectMapper.writeValueAsString(paymentPlanResponseV3);
        StubMapping stubMapping = wireMockRule.stubFor(
                post(urlEqualTo(stubUrl))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(responseBody)));
        Either<ResponseBody, PaymentPlanResponseV3> response = paymentPlansOperation.getPaymentPlansByClient(new HashMap<>(), paymentPlanRequestV3);
        assertFalse("No error present", response.isLeft());
        assertTrue("Get Payment Plan is not null", Objects.nonNull(response.get().getPaymentPlan()));
        assertFalse("Payment Plan List is not empty", response.get().getPaymentPlan().isEmpty());
        response.get().getPaymentPlan().stream().forEach(paymentV3 -> {
            assertThat(paymentV3.getTotalDue(), is(new BigDecimal("728000")));
            assertThat(paymentV3.getPrincipalDue(), is(new BigDecimal("728000")));
        });
    }

    @Test
    public void sdkShouldReturnErrorWhenStatusNotOK() throws IOException {
        PaymentPlansOperation paymentPlansOperation = getPaymentPlanOperation("http://localhost:8981");
        String responseBody = "{ \"error\": \"Error in services\"}";
        StubMapping stubMapping = wireMockRule.stubFor(
                post(urlEqualTo(stubUrl))
                        .willReturn(aResponse()
                                .withStatus(403)
                                .withBody(responseBody)));
        Either<ResponseBody, PaymentPlanResponseV3> response = paymentPlansOperation.getPaymentPlansByClient(new HashMap<>(), paymentPlanRequestV3);
        assertTrue("error Present", response.isLeft());
        assertThat("Error message is right",response.getLeft().string(),is(responseBody));
    }

    @Test
    public void sdkShouldReturnErrorWhenServicesNotConnect() throws IOException {
        PaymentPlansOperation paymentPlansOperation = getPaymentPlanOperation("http://localhost:8982");
        String responseBody = "{ \"error\": \"Error Trying Connect to  Credit Services\"}";
        StubMapping stubMapping = wireMockRule.stubFor(
                post(urlEqualTo("/promissorynote/create-and-sign-bad"))
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withBody("Error Response")
                        ));
        Either<ResponseBody, PaymentPlanResponseV3> response = paymentPlansOperation.getPaymentPlansByClient(new HashMap<>(), paymentPlanRequestV3);
        assertTrue("error Present", response.isLeft());
        assertThat("Error message is right",response.getLeft().string(),is(responseBody));
    }

    private PaymentPlansOperation getPaymentPlanOperation(String s) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(s)
                .client(builder.build())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();
        return new RetrofitPaymentPlansOperation(retrofit);
    }


}
