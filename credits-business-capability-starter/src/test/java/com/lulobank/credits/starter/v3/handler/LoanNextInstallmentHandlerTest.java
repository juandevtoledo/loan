package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.nextinstallment.NextInstallmentResponse;
import com.lulobank.credits.v3.port.in.nextinstallment.GenerateNextInstallmentPort;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static com.lulobank.credits.starter.v3.util.EntitiesFactory.LoanInformationFactory.getNextInstallment;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.OK;

public class LoanNextInstallmentHandlerTest {

    @Mock
    private GenerateNextInstallmentPort generateNextInstallmentPort;
    private LoanNextInstallmentHandler loanNextInstallmentHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loanNextInstallmentHandler = new LoanNextInstallmentHandler(generateNextInstallmentPort);
    }

    @Test
    public void loanInformation_WhenUseCaseResponseSuccess() {
        when(generateNextInstallmentPort.execute(any())).thenReturn(Either.right(getNextInstallment()));
        ResponseEntity<AdapterResponse> responseEntity = loanNextInstallmentHandler.get(UUID.randomUUID().toString());
        assertThat(responseEntity.getStatusCode(), is(OK));
        NextInstallmentResponse nextInstallmentResponse = (NextInstallmentResponse) responseEntity.getBody();
        assetThatNextInstallmentResponse(nextInstallmentResponse);
    }

    @Test
    public void loanInformation_WhenUseCaseResponseError() {
        when(generateNextInstallmentPort.execute(any())).thenReturn(Either.left(CoreBankingError.defaultError()));
        ResponseEntity<AdapterResponse> responseEntity = loanNextInstallmentHandler.get(UUID.randomUUID().toString());
        assertThat(responseEntity.getStatusCode(), is(BAD_GATEWAY));
    }


    private void assetThatNextInstallmentResponse(NextInstallmentResponse nextInstallmentResponse) {
        assertThat(nextInstallmentResponse.getState(), is("ACTIVE"));
        assertThat(nextInstallmentResponse.getFlags().isAutomaticDebitActive(), is(true));
        assertThat(nextInstallmentResponse.getFlags().isMinimumPaymentActive(), is(true));
        assertThat(nextInstallmentResponse.getFlags().isPayNow(), is(false));
    }
}
