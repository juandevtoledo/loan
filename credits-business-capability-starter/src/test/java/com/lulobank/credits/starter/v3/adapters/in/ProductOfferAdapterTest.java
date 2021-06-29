package com.lulobank.credits.starter.v3.adapters.in;

import com.google.common.collect.ImmutableList;
import com.lulobank.credits.starter.v3.adapters.in.dto.ApprovedProductOffer;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.OfferInstallment;
import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferResponse;
import com.lulobank.credits.starter.v3.handler.ProductOfferHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_CREDIT;
import static com.lulobank.credits.starter.utils.Samples.buildProductOfferResponse;
import static com.lulobank.credits.starter.utils.Samples.buildRequest;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

public class ProductOfferAdapterTest {

    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodParameter methodParameter;
    @Mock
    private ProductOfferHandler productOfferHandler;
    private ProductOfferAdapter productOfferAdapter;
    private HttpHeaders headers;
    private ProductOfferRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        productOfferAdapter = new ProductOfferAdapter(productOfferHandler);

        headers = new HttpHeaders();
        request = buildRequest();
    }

    @Test
    public void shouldReturnResponseEntityOk() {
        when(productOfferHandler.generateProductOffer(request, ID_CLIENT, headers))
                .thenReturn(ResponseEntity.ok(buildProductOfferResponse()));

        ResponseEntity<AdapterResponse> response = productOfferAdapter.getApprovedOffers(headers, ID_CLIENT, request);

        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getStatusCode(), is(OK));

        ProductOfferResponse responseBody = (ProductOfferResponse) response.getBody();
        assertThat(responseBody, notNullValue());
        assertThat(responseBody.getAmount(), is(11000000d));
        assertThat(responseBody.getCurrentDate(), notNullValue());
        assertThat(responseBody.getIdCredit(), is(ID_CREDIT));
        assertThat(responseBody.getOffers(), hasSize(1));
        assertApprovedOffer(responseBody.getOffers().get(0));
    }

    @Test
    public void shouldReturnErrorWhenBindingResultNotEmpty() {
        when(bindingResult.getAllErrors()).thenReturn(ImmutableList.of(new ObjectError("status", "status is null or empty")));

        ErrorResponse response = productOfferAdapter
                .handleValidationExceptions(new MethodArgumentNotValidException(methodParameter, bindingResult));

        assertThat(response, notNullValue());
        assertThat(response.getFailure(), is("400"));
        assertThat(response.getDetail(), is("V"));
        assertThat(response.getCode(), is("CRE_104"));
    }

    @Test
    public void shouldReturnErrorWhenBindingResultErrorsEmpty() {
        when(bindingResult.getAllErrors()).thenReturn(emptyList());

        ErrorResponse response = productOfferAdapter
                .handleValidationExceptions(new MethodArgumentNotValidException(methodParameter, bindingResult));

        assertThat(response, notNullValue());
        assertThat(response.getFailure(), is("500"));
        assertThat(response.getDetail(), is("U"));
        assertThat(response.getCode(), is("CRE_100"));
    }

    private void assertApprovedOffer(ApprovedProductOffer productOffer) {
        assertThat(productOffer, notNullValue());
        assertThat(productOffer.getAmount(), is(11000000d));
        assertThat(productOffer.getIdOffer(), notNullValue());
        assertThat(productOffer.getInsuranceCost(), is(0.0026d));
        assertThat(productOffer.getInterestRate(), is(16.5f));
        assertThat(productOffer.getMonthlyNominalRate(), is(1.281f));
        assertThat(productOffer.getName(), is("Crédito personalizado"));
        assertThat(productOffer.getType(), is("FLEXIBLE_LOAN"));
        assertThat(productOffer.getSimulateInstallment(),
                hasItems(samePropertyValuesAs(new OfferInstallment(1, 30000d)),
                        samePropertyValuesAs(new OfferInstallment(5, 30000d)),
                        samePropertyValuesAs(new OfferInstallment(10, 30000d))));
    }
}