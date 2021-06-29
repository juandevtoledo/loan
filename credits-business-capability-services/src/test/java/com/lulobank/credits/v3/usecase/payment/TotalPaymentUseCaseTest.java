package com.lulobank.credits.v3.usecase.payment;

import com.lulobank.credits.v3.events.GoodStandingCertificateEvent;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.ReportingQueueService;
import com.lulobank.credits.v3.service.CloseLoanService;
import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.usecase.payment.command.TotalPaymentInstallment;
import com.lulobank.credits.v3.vo.AdapterCredentials;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.lulobank.credits.Constants.CLIENT_ID;
import static com.lulobank.credits.Constants.CREDIT_ID;
import static com.lulobank.credits.Constants.LOAN_ID;
import static com.lulobank.credits.Constants.MAMBU_ACCOUNT_ID;
import static com.lulobank.credits.v3.port.in.loan.LoanState.CLOSED;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.foundCreditsEntityInBD;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.paymentResponseBuilder;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TotalPaymentUseCaseTest {

    @Mock
    private LoanPaymentService loanPaymentService;
    @Mock
    private CloseLoanService closeLoanService;
    @Captor
    private ArgumentCaptor<String> idCreditCaptor;
    @Captor
    ArgumentCaptor<String> idLoanCbsCaptor;
    private TotalPaymentUseCase totalPaymentUseCase;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        totalPaymentUseCase = new TotalPaymentUseCase(loanPaymentService, closeLoanService);
    }

    @Test
    public void makeTotalPayment() {
        when(closeLoanService.close(idCreditCaptor.capture(), idLoanCbsCaptor.capture())).thenReturn(Either.right(foundCreditsEntityInBD()));
        when(loanPaymentService.makePayment(any())).thenReturn(Either.right(paymentResponseBuilder().build()));
        Either<UseCaseResponseError, Boolean> response = totalPaymentUseCase.execute(getTotalPaymentInstallment());
        assertThat(response.isRight(), is(true));
        assertThat(response.get(), is(true));
        assertThat(idCreditCaptor.getValue(),is(CREDIT_ID));
        assertThat(idLoanCbsCaptor.getValue(),is(MAMBU_ACCOUNT_ID));
    }

    @Test
    public void makeTotalPaymentFailedSinceLoanServiceError() {
        TotalPaymentInstallment totalPaymentInstallment = getTotalPaymentInstallment();
        when(loanPaymentService.makePayment(any())).thenReturn(Either.left(new UseCaseResponseError("CRE_101", "01")));
        Either<UseCaseResponseError, Boolean> response = totalPaymentUseCase.execute(totalPaymentInstallment);
        assertThat(response.isRight(), is(false));
        assertThat(response.getLeft().getBusinessCode(), is("CRE_101"));
    }

    private TotalPaymentInstallment getTotalPaymentInstallment() {
        return TotalPaymentInstallment.builder()
                .amount(BigDecimal.valueOf(300000))
                .clientId(CLIENT_ID)
                .coreCbsId(MAMBU_ACCOUNT_ID)
                .creditId(CREDIT_ID)
                .adapterCredentials(new AdapterCredentials(new HashMap<>()))
                .build();
    }
}
