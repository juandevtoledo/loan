package com.lulobank.credits.starter.v3.adapters.in;

import com.google.common.collect.ImmutableList;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.PreapprovedLoanOffersRequest;
import com.lulobank.credits.starter.v3.handler.PreapprovedLoanOfferHandler;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.math.BigDecimal;

import static com.lulobank.credits.starter.utils.Constants.AMOUNT;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Samples.preapprovedLoanOffersResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PreapprovedLoanOfferAdapterTest {

    @Mock
    private PreapprovedLoanOfferHandler preapprovedLoanOfferHandler;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodParameter methodParameter;
    private PreapprovedLoanOfferAdapter preapprovedLoanOfferAdapter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        preapprovedLoanOfferAdapter = new PreapprovedLoanOfferAdapter(preapprovedLoanOfferHandler);
    }

    @Test
    public void responseEntity_WhenHandlerResponseSuccess() throws IOException {
        when(preapprovedLoanOfferHandler.generateProductOffer(any(), anyString())).thenReturn(AdapterResponseUtil.ok(preapprovedLoanOffersResponse()));
        ResponseEntity<AdapterResponse> responseResponseEntity = preapprovedLoanOfferAdapter.generateOffers(new HttpHeaders(), ID_CLIENT, new PreapprovedLoanOffersRequest(BigDecimal.valueOf(AMOUNT)), bindingResult);
        assertThat(responseResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void responseEntity_WhenBeanValidationError() throws IOException {
        when(bindingResult.getAllErrors()).thenReturn(ImmutableList.of(new ObjectError("clientLoanRequestedAmount", "clientLoanRequestedAmount is null or empty")));
        ErrorResponse errorResponse = preapprovedLoanOfferAdapter
                .handleValidationExceptions(new MethodArgumentNotValidException(methodParameter, bindingResult));
        Assert.assertThat(errorResponse.getFailure(), Matchers.is("400"));
        Assert.assertThat(errorResponse.getDetail(), Matchers.is("V"));
        Assert.assertThat(errorResponse.getCode(), Matchers.is("CRE_104"));
    }
}
