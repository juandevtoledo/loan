package com.lulobank.credits.v3.usecase.payment;

import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.usecase.payment.command.MinPaymentInstallment;
import com.lulobank.credits.v3.usecase.payment.command.TotalPaymentInstallment;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static com.lulobank.credits.Constants.CLIENT_ID;
import static com.lulobank.credits.Constants.LOAN_ID;
import static com.lulobank.credits.Constants.MAMBU_ACCOUNT_ID;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.paymentResponseBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MinimumPaymentUseCaseTest {

    @Mock
    private LoanPaymentService loanPaymentService;
    private MinimumPaymentUseCase minimumPaymentUseCase;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        minimumPaymentUseCase = new MinimumPaymentUseCase(loanPaymentService);
    }

    @Test
    public void makeMinimumPayment() {
        MinPaymentInstallment minPaymentInstallment = getPaymentInstallment();
        when(loanPaymentService.makePayment(any())).thenReturn(Either.right(paymentResponseBuilder().build()));
        Either<UseCaseResponseError, Boolean> response = minimumPaymentUseCase.execute(minPaymentInstallment);
        assertThat(response.isRight(), is(true));
        assertThat(response.get(), is(true));
    }

    @Test
    public void makeMinimumPaymentFailedSinceServiceError() {
        MinPaymentInstallment minPaymentInstallment = getPaymentInstallment();
        when(loanPaymentService.makePayment(any())).thenReturn(Either.left(new UseCaseResponseError("CRE_101", "01")));
        Either<UseCaseResponseError, Boolean> response = minimumPaymentUseCase.execute(minPaymentInstallment);
        assertThat(response.isRight(), is(false));
        assertThat(response.getLeft().getBusinessCode(), is("CRE_101"));
    }

    private MinPaymentInstallment getPaymentInstallment() {
        return MinPaymentInstallment.builder()
                .amount(BigDecimal.valueOf(300000))
                .clientId(CLIENT_ID)
                .coreCbsId(MAMBU_ACCOUNT_ID)
                .creditId(LOAN_ID)
                .build();
    }


}
