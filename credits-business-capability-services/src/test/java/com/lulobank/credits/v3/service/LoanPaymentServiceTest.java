package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.CreatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.credits.Constants.LOAN_AMOUNT;
import static com.lulobank.credits.v3.port.out.corebanking.CoreBankingErrorStatus.CRE_106;
import static com.lulobank.credits.v3.port.out.corebanking.CoreBankingErrorStatus.CRE_109;
import static com.lulobank.credits.v3.port.out.corebanking.dto.TypePayment.NUMBER_INSTALLMENTS;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.foundCreditsEntityInBD;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.clientAccountBuilder;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.loanPaymentRequestBuilder;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.paymentResponseBuilder;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_103;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class LoanPaymentServiceTest {

    public static final String ClIENT_ID = "3db1db74-0238-4053-a5f5-8cc7acb7416b";
    @Mock
    private CoreBankingService coreBankingService;
    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Captor
    private ArgumentCaptor<CreatePayment> createPaymentCaptor;
    private LoanPaymentService loanPaymentService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loanPaymentService = new LoanPaymentService(coreBankingService, creditsV3Repository);
    }

    @Test
    public void makePayment() {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(), any())).thenReturn(Option.of(foundCreditsEntityInBD()));
        when(coreBankingService.payment(createPaymentCaptor.capture())).thenReturn(Either.right(paymentResponseBuilder().build()));
        when(coreBankingService.getAccountsByClient(anyString())).thenReturn(Either.right(List.of(clientAccountBuilder().build()).toJavaList()));
        Either<UseCaseResponseError, PaymentApplied> response = loanPaymentService.makePayment(loanPaymentRequestBuilder().idClient(ClIENT_ID).build());
        assertThat(response.isRight(), is(true));
        assertThat(response.get().getAmount(), is(LOAN_AMOUNT));
        assertThat(response.get().getStatus(), is("SUCCESS"));
        assertThat(response.get().getStatus(), is("SUCCESS"));
        assertThat(createPaymentCaptor.getValue().getType(), is(NUMBER_INSTALLMENTS));
    }

    @Test
    public void failedPaymentSinceAccountIsBlocked() {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(), any())).thenReturn(Option.of(foundCreditsEntityInBD()));
        when(coreBankingService.payment(any())).thenReturn(Either.right(paymentResponseBuilder().build()));
        when(coreBankingService.getAccountsByClient(anyString())).thenReturn(Either.right(List.of(clientAccountBuilder().status("LOCKED").build()).toJavaList()));
        Either<UseCaseResponseError, PaymentApplied> response = loanPaymentService.makePayment(loanPaymentRequestBuilder().idClient(ClIENT_ID).build());
        assertThat(response.isLeft(), is(true));
        assertThat(response.getLeft().getBusinessCode(), is(CRE_109.name()));
        assertThat(response.getLeft().getDetail(), is("P_CB"));
    }

    @Test
    public void failedPaymentSincePaymentServiceError() {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(), any())).thenReturn(Option.of(foundCreditsEntityInBD()));
        when(coreBankingService.payment(any())).thenReturn(Either.left(CoreBankingError.paymentError("401")));
        when(coreBankingService.getAccountsByClient(anyString())).thenReturn(Either.right(List.of(clientAccountBuilder().build()).toJavaList()));
        Either<UseCaseResponseError, PaymentApplied> response = loanPaymentService.makePayment(loanPaymentRequestBuilder().idClient(ClIENT_ID).build());
        assertThat(response.isLeft(), is(true));
        assertThat(response.getLeft().getBusinessCode(), is(CRE_106.name()));
        assertThat(response.getLeft().getProviderCode(), is("401"));
        assertThat(response.getLeft().getDetail(), is("P_CB"));
    }

    @Test
    public void payment_WhenGetAccountErrorService() {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(), any())).thenReturn(Option.of(foundCreditsEntityInBD()));
        when(coreBankingService.payment(any())).thenReturn(Either.right(paymentResponseBuilder().build()));
        when(coreBankingService.getAccountsByClient(anyString())).thenReturn(Either.left(CoreBankingError.getParametersError()));
        Either<UseCaseResponseError, PaymentApplied> response = loanPaymentService.makePayment(loanPaymentRequestBuilder().idClient(ClIENT_ID).build());
        assertThat(response.isLeft(), is(true));
        assertThat(response.getLeft().getBusinessCode(), is(CRE_103.name()));
        assertThat(response.getLeft().getDetail(), is("P_CB"));
    }
}
