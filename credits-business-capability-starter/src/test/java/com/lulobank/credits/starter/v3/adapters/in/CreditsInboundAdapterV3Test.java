package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.utils.Constants;
import com.lulobank.credits.v3.port.in.promissorynote.dto.SignPromissoryNoteResponse;
import com.lulobank.credits.v3.usecase.AcceptOfferV3UseCase;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static com.lulobank.credits.starter.utils.Samples.creditWithOfferV3RequestBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CreditsInboundAdapterV3Test {

    @Mock
    private AcceptOfferV3UseCase acceptOfferV3UseCase;
    @Mock
    private BindingResult bindingResult;
    private HttpHeaders headers;
    private CreditsInboundAdapterV3 testedClass;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        headers = new HttpHeaders();
        testedClass = new CreditsInboundAdapterV3(acceptOfferV3UseCase);
    }

    @Test
    public void acceptOffer() {
        when(acceptOfferV3UseCase.execute(any())).thenReturn(Either.right(new SignPromissoryNoteResponse(true)));
        ResponseEntity<Object> responseEntity = testedClass.acceptOffer(headers, Constants.ID_CLIENT, creditWithOfferV3RequestBuilder(), bindingResult);
        assertThat("Http Status is right", responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void noValidOffer() {
    	UseCaseResponseError error = new UseCaseResponseError("CRE_116", "providerCode", "detail");
        when(acceptOfferV3UseCase.execute(any())).thenReturn(Either.left(error));
        ResponseEntity<Object> responseEntity = testedClass.acceptOffer(headers, Constants.ID_CLIENT, creditWithOfferV3RequestBuilder(), bindingResult);
        assertThat("Http Status is right", responseEntity.getStatusCode(), is(HttpStatus.NOT_ACCEPTABLE));
    }
}
