package com.lulobank.credits.v3.usecase.payment;

import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.usecase.payment.command.CustomPaymentInstallment;
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

public class CustomPaymentUseCaseTest {

    @Mock
    private LoanPaymentService loanPaymentService;
    private CustomPaymentUseCase customPaymentUseCase;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        customPaymentUseCase = new CustomPaymentUseCase(loanPaymentService);
    }

    @Test
    public void makeCustomPayment() {
        CustomPaymentInstallment customPaymentInstallment = getPaymentCustomInstallment();
        when(loanPaymentService.makePayment(any())).thenReturn(Either.right(paymentResponseBuilder().build()));
        Either<UseCaseResponseError, Boolean> response = customPaymentUseCase.execute(customPaymentInstallment);
        assertThat(response.isRight(), is(true));
        assertThat(response.get(), is(true));
    }

    @Test
    public void makeMinimumFailedSinceServiceError() {
        CustomPaymentInstallment customPaymentInstallment = getPaymentCustomInstallment();
        when(loanPaymentService.makePayment(any())).thenReturn(Either.left(new UseCaseResponseError("CRE_101", "01")));
        Either<UseCaseResponseError, Boolean> response = customPaymentUseCase.execute(customPaymentInstallment);
        assertThat(response.isRight(), is(false));
        assertThat(response.getLeft().getBusinessCode(), is("CRE_101"));
    }

    private CustomPaymentInstallment getPaymentCustomInstallment() {
        return CustomPaymentInstallment.builder()
                .amount(BigDecimal.valueOf(300000))
                .clientId(CLIENT_ID)
                .coreCbsId(MAMBU_ACCOUNT_ID)
                .creditId(LOAN_ID)
                .type("AMOUNT_INSTALLMENTS")
                .build();
    }
}
