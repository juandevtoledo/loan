package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.v3.usecase.automaticdebitoption.AutomaticDebitOptionUseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static com.lulobank.credits.starter.v3.util.EntitiesFactory.AutomaticDebitFactory.updateAutomaticDebitOptiontRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

public class AutomaticDebitOptionHandlerTest {

    @Mock
    private AutomaticDebitOptionUseCase automaticDebitOptionUseCase;
    private AutomaticDebitOptionHandler automaticDebitOptionHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        automaticDebitOptionHandler = new AutomaticDebitOptionHandler(automaticDebitOptionUseCase);
    }

    @Test
    public void updateAutomaticDebitOption_WhenUseCaseResponseSuccess() {
        when(automaticDebitOptionUseCase.execute(any())).thenReturn(Either.right(true));
        ResponseEntity<AdapterResponse> responseEntity = automaticDebitOptionHandler.execute(
                updateAutomaticDebitOptiontRequest(), UUID.randomUUID().toString());

        assertThat(responseEntity.getStatusCode(), is(OK));
        assertFalse(responseEntity.hasBody());
    }

    @Test
    public void updateAutomaticDebitOption_WhenUseCaseResponseError() {
        when(automaticDebitOptionUseCase.execute(any())).thenReturn(Either.left(CreditsError.databaseError()));
        ResponseEntity<AdapterResponse> responseEntity = automaticDebitOptionHandler.execute(
                updateAutomaticDebitOptiontRequest(), UUID.randomUUID().toString());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse.getCode(), is("CRE_101"));
        assertThat(errorResponse.getDetail(), is("D"));
        assertThat(errorResponse.getFailure(), is("404"));
    }
}
