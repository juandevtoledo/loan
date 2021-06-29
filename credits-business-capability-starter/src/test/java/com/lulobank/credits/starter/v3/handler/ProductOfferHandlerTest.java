package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.ApprovedProductOffer;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.OfferInstallment;
import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferResponse;
import com.lulobank.credits.v3.port.in.productoffer.GenerateProductOfferPort;
import com.lulobank.credits.v3.usecase.productoffer.command.GenerateOfferRequest;
import com.lulobank.credits.v3.vo.CreditsError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_CREDIT;
import static com.lulobank.credits.starter.utils.Samples.buildProductOffer;
import static com.lulobank.credits.starter.utils.Samples.buildRequest;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class ProductOfferHandlerTest {

    @Mock
    private GenerateProductOfferPort generateProductOfferPort;
    private ProductOfferHandler productOfferHandler;
    private HttpHeaders headers;
    private ProductOfferRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        productOfferHandler = new ProductOfferHandler(generateProductOfferPort);

        headers = new HttpHeaders();
        request = buildRequest();
    }

    @Test
    public void shouldReturnResponseOk() {
        when(generateProductOfferPort.execute(any(GenerateOfferRequest.class))).thenReturn(Either.right(buildProductOffer()));

        ResponseEntity<AdapterResponse> responseEntity = productOfferHandler.generateProductOffer(request, ID_CLIENT, headers);

        assertThat(responseEntity, notNullValue());
        assertThat(responseEntity.getStatusCodeValue(), is(200));
        assertThat(responseEntity.getStatusCode(), is(OK));

        ProductOfferResponse offerResponse = (ProductOfferResponse) responseEntity.getBody();
        assertThat(offerResponse, notNullValue());
        assertThat(offerResponse.getAmount(), is(11000000d));
        assertThat(offerResponse.getCurrentDate(), notNullValue());
        assertThat(offerResponse.getIdCredit(), is(ID_CREDIT));
        assertThat(offerResponse.getOffers(), hasSize(1));
        assertApprovedOffer(offerResponse.getOffers().get(0));
    }

    @Test
    public void shouldNotReturnResponseWhenDataBaseError() {
        when(generateProductOfferPort.execute(any(GenerateOfferRequest.class))).thenReturn(Either.left(CreditsError.databaseError()));

        ResponseEntity<AdapterResponse> responseEntity = productOfferHandler.generateProductOffer(request, ID_CLIENT, headers);

        assertThat(responseEntity, notNullValue());
        assertThat(responseEntity.getStatusCodeValue(), is(404));
        assertThat(responseEntity.getStatusCode(), is(NOT_FOUND));

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getFailure(), is("404"));
        assertThat(errorResponse.getDetail(), is("D"));
        assertThat(errorResponse.getCode(), is("CRE_101"));
    }

    @Test
    public void shouldNotReturnResponseWhenGenerateOfferError() {
        when(generateProductOfferPort.execute(any(GenerateOfferRequest.class))).thenReturn(Either.left(CreditsError.generateOfferError()));

        ResponseEntity<AdapterResponse> responseEntity = productOfferHandler.generateProductOffer(request, ID_CLIENT, headers);

        assertThat(responseEntity, notNullValue());
        assertThat(responseEntity.getStatusCodeValue(), is(406));
        assertThat(responseEntity.getStatusCode(), is(NOT_ACCEPTABLE));

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getFailure(), is("406"));
        assertThat(errorResponse.getDetail(), is("U"));
        assertThat(errorResponse.getCode(), is("CRE_102"));
    }

    @Test
    public void shouldNotReturnResponseWhenUnknownError() {
        when(generateProductOfferPort.execute(any(GenerateOfferRequest.class))).thenReturn(Either.left(CreditsError.unknownError()));

        ResponseEntity<AdapterResponse> responseEntity = productOfferHandler.generateProductOffer(request, ID_CLIENT, headers);

        assertThat(responseEntity, notNullValue());
        assertThat(responseEntity.getStatusCodeValue(), is(500));
        assertThat(responseEntity.getStatusCode(), is(INTERNAL_SERVER_ERROR));

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getFailure(), is("500"));
        assertThat(errorResponse.getDetail(), is("U"));
        assertThat(errorResponse.getCode(), is("CRE_100"));
    }

    private void assertApprovedOffer(ApprovedProductOffer approvedProductOffer) {
        assertThat(approvedProductOffer, notNullValue());
        assertThat(approvedProductOffer.getAmount(), is(11000000d));
        assertThat(approvedProductOffer.getIdOffer(), notNullValue());
        assertThat(approvedProductOffer.getInsuranceCost(), is(0.0026d));
        assertThat(approvedProductOffer.getInterestRate(), is(16.5f));
        assertThat(approvedProductOffer.getMonthlyNominalRate(), is(1.281f));
        assertThat(approvedProductOffer.getName(), is("Crédito personalizado"));
        assertThat(approvedProductOffer.getType(), is("FLEXIBLE_LOAN"));
        assertThat(approvedProductOffer.getSimulateInstallment(),
                hasItems(samePropertyValuesAs(new OfferInstallment(1, 30000d)),
                        samePropertyValuesAs(new OfferInstallment(5, 30000d)),
                        samePropertyValuesAs(new OfferInstallment(10, 30000d))));
    }

}