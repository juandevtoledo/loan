package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.CreatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.port.out.corebanking.dto.TypePayment;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;
import com.lulobank.credits.v3.usecase.automaticdebit.command.ProcessPayment;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanActive;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.clientAccountBuilder;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.processPayment;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class FailurePaymentServiceTest {
    @Mock
    private CoreBankingService coreBankingService;
    @Mock
    private SchedulerTransactionAsyncService schedulerAsyncService;
    @Captor
    private ArgumentCaptor<CreatePayment> createPaymentArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> coreBankingCaptor;
    @Captor
    private ArgumentCaptor<TransactionRequest> transactionRequestCaptor;
    private FailurePaymentService failurePaymentService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        failurePaymentService = new FailurePaymentService(coreBankingService, schedulerAsyncService);
    }


    @Test
    public void retry_WhenPaymentSuccessful() {
        when(coreBankingService.getAccountsByClient(coreBankingCaptor.capture())).thenReturn(Either.right(List.of(clientAccountBuilder().build()).asJava()));
        when(coreBankingService.payment(createPaymentArgumentCaptor.capture())).thenReturn(Either.right(PaymentApplied.builder().build()));
        Try<ProcessPayment> response = failurePaymentService.retry(processPayment(loanActive(), creditsEntityWithAcceptOffer(), "metadata"));
        assertThat(response.isSuccess(), is(true));
        Mockito.verify(schedulerAsyncService, times(1)).retryTransaction(transactionRequestCaptor.capture());
        assertTransactionRequest(transactionRequestCaptor.getValue());
        assertCreatePayment(createPaymentArgumentCaptor.getValue());
    }

    @Test
    public void retry_WhenGetAccountsFailed() {
        when(coreBankingService.getAccountsByClient(coreBankingCaptor.capture())).thenReturn(Either.left(CoreBankingError.clientWithOutAccountsError()));
        Try<ProcessPayment> response = failurePaymentService.retry(processPayment(loanActive(), creditsEntityWithAcceptOffer(), "metadata"));
        assertThat(response.isFailure(), is(true));
        Mockito.verify(coreBankingService, never()).payment(any());
        Mockito.verify(schedulerAsyncService, times(1)).retryTransaction(transactionRequestCaptor.capture());
    }

    @Test
    public void retry_WhenPaymentFailed() {
        when(coreBankingService.getAccountsByClient(coreBankingCaptor.capture())).thenReturn(Either.right(List.of(clientAccountBuilder().build()).asJava()));
        when(coreBankingService.payment(createPaymentArgumentCaptor.capture())).thenReturn(Either.left(CoreBankingError.accountBlocked()));
        Try<ProcessPayment> response = failurePaymentService.retry(processPayment(loanActive(), creditsEntityWithAcceptOffer(), "metadata"));
        assertThat(response.isFailure(), is(true));
        Mockito.verify(schedulerAsyncService, times(1)).retryTransaction(transactionRequestCaptor.capture());
    }

    @Test
    public void retry_WhenNoneAccountActive() {
        when(coreBankingService.getAccountsByClient(coreBankingCaptor.capture()))
                .thenReturn(Either.right(List.of(clientAccountBuilder().status("BLOCK").build()).asJava()));
        Try<ProcessPayment> response = failurePaymentService.retry(processPayment(loanActive(), creditsEntityWithAcceptOffer(), "metadata"));
        assertThat(response.isFailure(), is(true));
        Mockito.verify(coreBankingService, never()).payment(any());
        Mockito.verify(schedulerAsyncService, times(1)).retryTransaction(transactionRequestCaptor.capture());
    }


    private void assertCreatePayment(CreatePayment createPayment) {
        assertThat(createPayment.getLoanId(), is("YAMW127"));
        assertThat(createPayment.getAccountId(), is("26262626"));
        assertThat(createPayment.getCoreBankingId(), is("1999368732"));
        assertThat(createPayment.getType(), is(TypePayment.NONE));
        assertThat(createPayment.getPayOff(), is(false));
        assertThat(createPayment.getAmount(), is(BigDecimal.valueOf(3500000).divide(BigDecimal.valueOf(1.004),
                2, BigDecimal.ROUND_DOWN)));
    }

    private void assertTransactionRequest(TransactionRequest transactionRequest) {
        assertThat(transactionRequest.getDayOfPay(), is(15));
        assertThat(transactionRequest.getIdClient(), is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
        assertThat(transactionRequest.getMetadata(), is("metadata"));
    }

}
