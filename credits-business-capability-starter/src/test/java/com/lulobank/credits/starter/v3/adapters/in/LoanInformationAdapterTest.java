package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.handler.LoanDetailHandler;
import com.lulobank.credits.starter.v3.handler.LoanMovementsHandler;
import com.lulobank.credits.starter.v3.handler.LoanNextInstallmentHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static com.lulobank.credits.starter.utils.Constants.LIMIT;
import static com.lulobank.credits.starter.utils.Constants.OFFSET;
import static com.lulobank.credits.starter.v3.util.EntitiesFactory.LoanInformationFactory.getNextInstallmentResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

public class LoanInformationAdapterTest {

    @Mock
    private LoanNextInstallmentHandler loanNextInstallmentHandler;
    @Mock
    private LoanDetailHandler loanDetailHandler;
    @Mock
    private LoanMovementsHandler loanMovementsHandler;
    private LoanInformationAdapter loanInformationAdapter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loanInformationAdapter = new LoanInformationAdapter(loanDetailHandler, loanNextInstallmentHandler, loanMovementsHandler);
    }

    @Test
    public void nextInstallment_WhenHandlerResponseSuccess() {
        when(loanNextInstallmentHandler.get(any())).thenReturn(ResponseEntity.ok(getNextInstallmentResponse()));
        ResponseEntity<AdapterResponse> responseEntity = loanInformationAdapter.nextInstallments(new HttpHeaders(), UUID.randomUUID().toString());
        assertThat(responseEntity.getStatusCode(), is(OK));
    }

    @Test
    public void detailCredit_WhenHandlerResponseSuccess() {
        when(loanDetailHandler.get(any())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<AdapterResponse> responseEntity = loanInformationAdapter.loanDetail(new HttpHeaders(), UUID.randomUUID().toString());
        assertThat(responseEntity.getStatusCode(), is(OK));
    }

    @Test
    public void getLoanMovements_WhenHandlerResponseSuccess() {
        when(loanMovementsHandler.getLoanMovements(any(), any(), any())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<AdapterResponse> responseEntity = loanInformationAdapter.getLoanMovements(new HttpHeaders(),
                UUID.randomUUID().toString(), OFFSET, LIMIT );
        assertThat(responseEntity.getStatusCode(), is(OK));
    }
}
