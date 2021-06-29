package com.lulobank.credits.starter.v3.adapters.in.automaticdebitoption;

import com.google.common.collect.ImmutableList;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.handler.AutomaticDebitOptionHandler;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.UUID;

import static com.lulobank.credits.starter.v3.util.EntitiesFactory.AutomaticDebitFactory.updateAutomaticDebitOptiontRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

public class AutomaticDebitOptionAdapterTest {

    @Mock
    private AutomaticDebitOptionHandler automaticDebitOptionHandler;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodParameter methodParameter;
    private AutomaticDebitOptionAdapter automaticDebitOptionAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        automaticDebitOptionAdapter = new AutomaticDebitOptionAdapter(automaticDebitOptionHandler);
    }

    @Test
    public void updateAutomaticDebitOption_WhenHandlerResponseSuccess() {
        when(automaticDebitOptionHandler.execute(any(), any())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<AdapterResponse> responseEntity = automaticDebitOptionAdapter.updateAutomaticDebitOption(
                UUID.randomUUID().toString(), updateAutomaticDebitOptiontRequest());
        assertFalse(responseEntity.hasBody());
        assertThat(responseEntity.getStatusCode(), is(OK));
    }

    @Test
    public void updateAutomaticDebitOption_WhenHandlerResponseError() {
        when(automaticDebitOptionHandler.execute(any(), any())).thenReturn(ResponseEntity.badRequest().build());
        ResponseEntity<AdapterResponse> responseEntity = automaticDebitOptionAdapter.updateAutomaticDebitOption(
                UUID.randomUUID().toString(), updateAutomaticDebitOptiontRequest());
        assertFalse(responseEntity.hasBody());
        assertThat(responseEntity.getStatusCode(), is(BAD_REQUEST));
    }

    @Test
    public void shouldReturnErrorWhenBindingResultNotEmpty() {
        when(bindingResult.getAllErrors()).thenReturn(ImmutableList.of(new ObjectError("automaticDebit",
                "automaticDebit is null")));
        ErrorResponse response = automaticDebitOptionAdapter
                .handleValidationExceptions(new MethodArgumentNotValidException(methodParameter, bindingResult));
        Assert.assertThat(response, notNullValue());
        Assert.assertThat(response.getFailure(), Matchers.is("400"));
        Assert.assertThat(response.getDetail(), Matchers.is("V"));
        Assert.assertThat(response.getCode(), Matchers.is("CRE_104"));
    }
}
