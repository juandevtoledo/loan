package com.lulobank.credits.sdk.operations.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.credits.sdk.dto.CreditErrorResult;
import com.lulobank.credits.sdk.dto.CreditSuccessResult;
import com.lulobank.credits.sdk.dto.clientloandetail.Loan;
import com.lulobank.credits.sdk.dto.clientloandetail.LoanDetail;
import com.lulobank.credits.sdk.dto.paymentplan.PaymentDetail;
import com.lulobank.credits.sdk.dto.paymentplan.PaymentPlan;
import com.lulobank.utils.exception.ServiceException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class GetLoanDetailOperationsTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8981);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Value("classpath:mocks/getclientloan/GetClientLoan-Response.json")
    private Resource responseGetClientLoan;

    @Test
    public void getLoanCreditInfoOk() throws IOException {
        RetrofitGetLoanDetailOperations retrofitGetLoan = new RetrofitGetLoanDetailOperations("http://localhost:8981");
        LoanDetail loanDetail = new LoanDetail();
        loanDetail.setIdCredit("bee0a718-c807-4539-a3fc-c2879d76d7db");
        loanDetail.setIdCreditCBS("ZKFE749");
        loanDetail.setAmount(1000000.0);
        Loan loan = new Loan();
        loan.setLoanDetail(loanDetail);
        List<Loan> listLoan = new ArrayList<>();
        listLoan.add(loan);
        CreditSuccessResult<List<Loan>> creditSuccessResultRequest= new CreditSuccessResult<>(listLoan);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String jsonStr = mapper.writeValueAsString(creditSuccessResultRequest);
        wireMockRule
                .stubFor(get(urlMatching("/credits/loan/client/09004a93-2905-483a-9b9a-9d29bca50b58"))
                        .willReturn(aResponse()
                                    .withStatus(HttpStatus.OK.value())
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(jsonStr)
                                    )
                        );
        Map<String, String> headers = new HashMap<>();
        List<Loan> list = retrofitGetLoan.getClientLoan(headers,"09004a93-2905-483a-9b9a-9d29bca50b58");
        assertNotNull(list);
        assertEquals(1,list.size());
    }

    @Test
    public void getLoanCreditException() throws IOException {
        RetrofitGetLoanDetailOperations retrofitGetLoan = new RetrofitGetLoanDetailOperations("http://localhost:8981");
        wireMockRule
                .stubFor(get(urlMatching("/credits/loan/client/09004a93-2905-483a-9b9a-9d29bca50b58"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody("")
                        )
                );
        exceptionRule.expect(ServiceException.class);
        exceptionRule.expectMessage("");
        Map<String, String> headers = new HashMap<>();
        List<Loan> list = retrofitGetLoan.getClientLoan(headers,"09004a93-2905-483a-9b9a-9d29bca50b58");
    }


}
