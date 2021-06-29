package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.PreapprovedLoanOffersRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.PreapprovedLoanOffersResponse;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.PreapprovedLoanOffersUseCase;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.command.GetOffersByIdClient;
import com.lulobank.credits.v3.vo.CreditsError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static com.lulobank.credits.starter.utils.Constants.AMOUNT;
import static com.lulobank.credits.starter.utils.Samples.offeredResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class PreapprovedLoanOfferHandlerTest {

    private PreapprovedLoanOfferHandler preapprovedLoanOfferHandler;
    @Mock
    private PreapprovedLoanOffersUseCase preapprovedLoanOffersUseCase;

    @Captor
    private ArgumentCaptor<GetOffersByIdClient> getOffersByIdClientCaptor;
    private final String ID_CLIENT = UUID.randomUUID().toString();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        preapprovedLoanOfferHandler = new PreapprovedLoanOfferHandler(preapprovedLoanOffersUseCase);
    }

    @Test
    public void handlerResponse_WhenUseCaseResponseSuccess() throws IOException {
        PreapprovedLoanOffersRequest request = new PreapprovedLoanOffersRequest(BigDecimal.valueOf(AMOUNT));
        when(preapprovedLoanOffersUseCase.execute(getOffersByIdClientCaptor.capture())).thenReturn(Either.right(offeredResponse()));
        ResponseEntity<AdapterResponse> response = preapprovedLoanOfferHandler.generateProductOffer(request, ID_CLIENT);
        PreapprovedLoanOffersResponse preapprovedLoanOffersResponse = (PreapprovedLoanOffersResponse) response.getBody();
        assertThat(preapprovedLoanOffersResponse.getAmount(), is(BigDecimal.valueOf(AMOUNT)));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void handlerResponse_WhenUseCaseResponseCoreBankingError() throws IOException {
        PreapprovedLoanOffersRequest request = new PreapprovedLoanOffersRequest(BigDecimal.valueOf(AMOUNT));
        when(preapprovedLoanOffersUseCase.execute(getOffersByIdClientCaptor.capture())).thenReturn(Either.left(CoreBankingError.getParametersError()));
        ResponseEntity<AdapterResponse> response = preapprovedLoanOfferHandler.generateProductOffer(request, ID_CLIENT);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
    }

    @Test
    public void handlerResponse_WhenUseCaseResponsePersistError() throws IOException {
        PreapprovedLoanOffersRequest request = new PreapprovedLoanOffersRequest(BigDecimal.valueOf(AMOUNT));
        when(preapprovedLoanOffersUseCase.execute(getOffersByIdClientCaptor.capture())).thenReturn(Either.left(CreditsError.persistError()));
        ResponseEntity<AdapterResponse> response = preapprovedLoanOfferHandler.generateProductOffer(request, ID_CLIENT);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
    }
}
