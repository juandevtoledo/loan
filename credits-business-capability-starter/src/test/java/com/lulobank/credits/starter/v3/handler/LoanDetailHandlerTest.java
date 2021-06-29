package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.loan.LoanDetailResponse;
import com.lulobank.credits.v3.usecase.loandetail.LoanDetailUseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.lulobank.credits.starter.v3.util.EntitiesFactory.LoanInformationFactory.getLoanDetail;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class LoanDetailHandlerTest {

    @Mock
    private LoanDetailUseCase loanDetailUseCase;
    private LoanDetailHandler loanDetailHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loanDetailHandler = new LoanDetailHandler(loanDetailUseCase);
    }

    @Test
    public void loanDetail_WhenUseCaseResponseSuccess() {
        when(loanDetailUseCase.execute(any())).thenReturn(Either.right(getLoanDetail()));
        ResponseEntity<AdapterResponse> responseEntity = loanDetailHandler.get(UUID.randomUUID().toString());
        assertThat(responseEntity.getStatusCode(), is(OK));
        LoanDetailResponse loanDetailResponse = (LoanDetailResponse) responseEntity.getBody();
        assertThatLoanDetailResponse(loanDetailResponse);
    }

    @Test
    public void loanDetail_WhenUseCaseResponseError() {
        when(loanDetailUseCase.execute(any())).thenReturn(Either.left(CreditsError.databaseError()));
        ResponseEntity<AdapterResponse> responseEntity = loanDetailHandler.get(UUID.randomUUID().toString());
        assertThat(responseEntity.getStatusCode(), is(NOT_FOUND));
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse.getCode(), is("CRE_101"));
        assertThat(errorResponse.getDetail(), is("D"));
        assertThat(errorResponse.getFailure(), is("404"));
    }

    private void assertThatLoanDetailResponse(LoanDetailResponse loanDetailResponse) {
        assertThat(loanDetailResponse.getIdCredit(),is("0467b237-925a-40d6-aa57-7dcdce68681f"));
        assertThat(loanDetailResponse.getIdLoanCBS(),is("79624120546"));
        assertThat(loanDetailResponse.getCreateOn(),is(LocalDateTime.parse("2021-02-15 20:16", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        assertThat(loanDetailResponse.getClosedDate(),is(LocalDateTime.parse("2021-02-15 20:16", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        assertThat(loanDetailResponse.getInstallments(),is(48));
        assertThat(loanDetailResponse.getPaidInstallments(),is(0));
        assertThat(loanDetailResponse.getPaidAmount().getValue(),is(ZERO.setScale(2)));
        assertThat(loanDetailResponse.getPaidAmount().getCurrency(),is("COP"));
        assertThat(loanDetailResponse.getRequestedAmount().getValue(),is(BigDecimal.valueOf(12300000.00).setScale(2)));
        assertThat(loanDetailResponse.getRequestedAmount().getCurrency(),is("COP"));
        assertThat(loanDetailResponse.getState(),is("ACTIVE"));
        assertThat(loanDetailResponse.getRates().getAnnualEffective(),is(BigDecimal.valueOf(16.50).setScale(2)));
        assertThat(loanDetailResponse.getRates().getMonthlyNominal(),is(BigDecimal.valueOf(1.28)));
        assertThat(loanDetailResponse.getPaymentPlanList().isEmpty(),is(false));
    }
}
