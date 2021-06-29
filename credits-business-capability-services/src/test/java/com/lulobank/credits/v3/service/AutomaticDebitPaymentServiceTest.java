package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.ClientAccount;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;
import com.lulobank.credits.v3.service.dto.LoanPaymentRequest;
import com.lulobank.credits.v3.usecase.automaticdebit.command.ProcessPayment;
import com.lulobank.credits.v3.vo.CreditsError;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanActive;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.foundCreditsEntityInBD;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.clientAccountBuilder;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.paymentResponseBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class AutomaticDebitPaymentServiceTest {

    @Mock
    private LoanPaymentService loanPaymentService;
    @Mock
    private CloseLoanService closeLoanService;
    @Mock
    private SchedulerTransactionAsyncService schedulerTransactionAsyncService;
    @Mock
    private CoreBankingService coreBankingService;
    private FailurePaymentService failurePaymentService;
    @Captor
    private ArgumentCaptor<LoanPaymentRequest> loanPaymentRequestCaptor;
    @Captor
    private ArgumentCaptor<String> idCreditCaptor;
    @Captor
    private ArgumentCaptor<String> idLoanAccountCaptor;
    @Captor
    private ArgumentCaptor<TransactionRequest> transactionRequestCaptor;
    private AutomaticDebitPaymentService automaticDebitPaymentService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        failurePaymentService = new FailurePaymentService(coreBankingService , schedulerTransactionAsyncService);
        automaticDebitPaymentService =
                new AutomaticDebitPaymentService(loanPaymentService, closeLoanService, schedulerTransactionAsyncService, failurePaymentService);
    }

    @Test
    public void payment_WhenPaymentInCoreBankingSuccess() {
        CreditsV3Entity creditsV3Entity = creditsEntityWithAcceptOffer();
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.right(paymentResponseBuilder().build()));
        when(closeLoanService.close(idCreditCaptor.capture(), idLoanAccountCaptor.capture())).thenReturn(Either.right(foundCreditsEntityInBD()));
        Try<ProcessPayment> response = automaticDebitPaymentService.payment(processPayment(loanActive(), creditsV3Entity, "metadata"));
        assertThat(response.isSuccess(), is(true));
        assertCloseLoanCaptors();
        assertLoanPaymentRequestCaptor(loanPaymentRequestCaptor.getValue());
        Mockito.verify(schedulerTransactionAsyncService, never()).deleteTransaction(transactionRequestCaptor.capture());
    }

    @Test
    public void payment_WhenPaymentInCoreBankingFailed() {
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.left(CoreBankingError.defaultError()));
        when(coreBankingService.getAccountsByClient(any())).thenReturn(Either.right(List.of(clientAccountBuilder().build()).asJava()));
        when(coreBankingService.payment(any())).thenReturn(Either.right(PaymentApplied.builder().build()));
        Try<ProcessPayment> response = automaticDebitPaymentService.payment(processPayment(loanActive(), creditsEntityWithAcceptOffer(), "metadata"));
        assertThat(response.isSuccess(), is(true));
        Mockito.verify(closeLoanService, never()).close(anyString(), anyString());
        Mockito.verify(schedulerTransactionAsyncService, never()).deleteTransaction(transactionRequestCaptor.capture());
        Mockito.verify(schedulerTransactionAsyncService, times(1)).retryTransaction(transactionRequestCaptor.capture());
    }

    @Test
    public void payment_WhenRetryPaymentFailed() {
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.left(CoreBankingError.defaultError()));
        when(coreBankingService.getAccountsByClient(any())).thenReturn(Either.right(List.of(clientAccountBuilder().build()).asJava()));
        when(coreBankingService.payment(any())).thenReturn(Either.left(CoreBankingError.defaultError()));
        Try<ProcessPayment> response = automaticDebitPaymentService.payment(processPayment(loanActive(), creditsEntityWithAcceptOffer(), "metadata"));
        assertThat(response.isFailure(), is(true));
        Mockito.verify(closeLoanService, never()).close(anyString(), anyString());
        Mockito.verify(schedulerTransactionAsyncService, times(1)).retryTransaction(transactionRequestCaptor.capture());
    }

    @Test
    public void payment_WhenCloseLoanServiceFailed() {
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.right(paymentResponseBuilder().build()));
        when(closeLoanService.close(idCreditCaptor.capture(), idLoanAccountCaptor.capture())).thenReturn(Either.left(CreditsError.databaseError()));
        Try<ProcessPayment> response = automaticDebitPaymentService.payment(processPayment(loanActive(), creditsEntityWithAcceptOffer(), "metadata"));
        assertThat(response.isFailure(), is(true));
        Mockito.verify(schedulerTransactionAsyncService, never()).deleteTransaction(transactionRequestCaptor.capture());
        Mockito.verify(schedulerTransactionAsyncService, times(1)).retryTransaction(transactionRequestCaptor.capture());
    }

    @Test
    public void payment_WhenPaymentInOneTime() {
        CreditsV3Entity creditsV3Entity = creditsEntityWithAcceptOffer();
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.right(paymentResponseBuilder().build()));
        when(closeLoanService.close(idCreditCaptor.capture(), idLoanAccountCaptor.capture())).thenReturn(Either.right(foundCreditsEntityInBD()));
        Try<ProcessPayment> response = automaticDebitPaymentService.payment(processPayment(loanActive(), creditsV3Entity, "25#credits#ONE_TIME"));
        assertThat(response.isSuccess(), is(true));
        assertCloseLoanCaptors();
        assertLoanPaymentRequestCaptor(loanPaymentRequestCaptor.getValue());
        Mockito.verify(schedulerTransactionAsyncService, times(1)).deleteTransaction(transactionRequestCaptor.capture());
        assertTransactionRequest(transactionRequestCaptor.getValue());
    }

    private ProcessPayment processPayment(LoanInformation loanInformation, CreditsV3Entity creditsV3Entity, String metadata) {
        return ProcessPayment.builder()
                .loanInformation(loanInformation)
                .idCredit(creditsV3Entity.getIdCredit().toString())
                .idClient(creditsV3Entity.getIdClient())
                .idLoanAccountMambu(creditsV3Entity.getIdLoanAccountMambu())
                .dayOfPay(creditsV3Entity.getDayOfPay())
                .metadataEvent(metadata)
                .build();
    }

    private void assertCloseLoanCaptors() {
        assertThat(idCreditCaptor.getValue(), is("014176ef-e291-4db6-9b49-18d253a8ae5d"));
        assertThat(idLoanAccountCaptor.getValue(), is("YAMW127"));
    }

    private void assertLoanPaymentRequestCaptor(LoanPaymentRequest loanPaymentRequest) {
        assertThat(loanPaymentRequest.getAmount(), is(BigDecimal.valueOf(3500000)));
        assertThat(loanPaymentRequest.getIdClient(), is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
        assertThat(loanPaymentRequest.getIdCredit(), is("014176ef-e291-4db6-9b49-18d253a8ae5d"));
        assertThat(loanPaymentRequest.getPaymentOff(), is(true));
        assertThat(loanPaymentRequest.getLoanId(), is("YAMW127"));
    }

    private void assertTransactionRequest(TransactionRequest transactionRequest) {
        assertThat(transactionRequest.getDayOfPay(), is(15));
        assertThat(transactionRequest.getIdClient(), is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
        assertThat(transactionRequest.getMetadata(), is("25#credits#ONE_TIME"));
    }


}
