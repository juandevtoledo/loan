package com.lulobank.credits.starter.v3.util;

import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferResponse;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class AdapterResponseUtilTest {

    @Test
    public void shouldReturnResponseEntityOk() {
        ProductOfferResponse responseBody = ProductOfferResponse.builder().build();
        ResponseEntity<AdapterResponse> response = AdapterResponseUtil.ok(responseBody);

        assertThat(response, notNullValue());

        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), is(responseBody));
    }

    @Test
    public void shouldReturnResponseEntitySuccess() {
        ProductOfferResponse responseBody = ProductOfferResponse.builder().build();
        ResponseEntity<AdapterResponse> response = AdapterResponseUtil.success(responseBody, ACCEPTED);

        assertThat(response, notNullValue());

        assertThat(response.getStatusCode(), is(ACCEPTED));
        assertThat(response.getStatusCodeValue(), is(202));
        assertThat(response.getBody(), is(responseBody));
    }

    @Test
    public void shouldReturnResponseEntityError() {
        ErrorResponse responseBodyError = new ErrorResponse("FAILURE_TEST", "CODE_TEST");
        ResponseEntity<AdapterResponse> response = AdapterResponseUtil.error(responseBodyError, NOT_ACCEPTABLE);

        assertThat(response, notNullValue());

        assertThat(response.getStatusCode(), is(NOT_ACCEPTABLE));
        assertThat(response.getStatusCodeValue(), is(406));
        assertThat(response.getBody(), is(responseBodyError));
    }

    @Test
    public void shouldReturnHttpStatusFromBusinessCode() {
        assertThat(AdapterResponseUtil.getHttpStatusFromBusinessCode("CRE_100"), is(INTERNAL_SERVER_ERROR));
        assertThat(AdapterResponseUtil.getHttpStatusFromBusinessCode("CRE_101"), is(NOT_FOUND));
        assertThat(AdapterResponseUtil.getHttpStatusFromBusinessCode("CRE_102"), is(NOT_ACCEPTABLE));
        assertThat(AdapterResponseUtil.getHttpStatusFromBusinessCode("CRE_103"), is(BAD_GATEWAY));
        assertThat(AdapterResponseUtil.getHttpStatusFromBusinessCode("GEN_100"), is(INTERNAL_SERVER_ERROR));
    }
}