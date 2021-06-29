package com.lulobank.credits.v3.port.in.usecase;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.AmountCurrency;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerNotificationAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.CreateTransactionRequest;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;
import com.lulobank.credits.v3.service.AutomaticDebitPaymentService;
import com.lulobank.credits.v3.usecase.automaticdebit.MakeAutomaticPaymentUseCase;
import com.lulobank.credits.v3.usecase.automaticdebit.command.MakeAutomaticDebitCommand;
import com.lulobank.credits.v3.usecase.automaticdebit.command.ProcessPayment;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static com.lulobank.credits.v3.util.LoanMockFactory.*;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MakeAutomaticPaymentUseCaseTest {

    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private AutomaticDebitPaymentService automaticDebitPaymentService;
    @Mock
    private CoreBankingService coreBankingService;
    @Mock
    private SchedulerTransactionAsyncService schedulerAsyncService;
    @Mock
    private SchedulerNotificationAsyncService schedulerNotificationAsyncService;
    @Captor
    private ArgumentCaptor<TransactionRequest> transactionRequestCaptor;
    @Captor
    private ArgumentCaptor<CreateTransactionRequest> createTransactionRequestCaptor;
    @Captor
    private ArgumentCaptor<ProcessPayment> processPaymentCaptor;
    private MakeAutomaticPaymentUseCase makeAutomaticPaymentUseCase;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        makeAutomaticPaymentUseCase = new MakeAutomaticPaymentUseCase(creditsV3Repository, automaticDebitPaymentService,
                coreBankingService, schedulerAsyncService, schedulerNotificationAsyncService);
    }

    @Test
    public void automaticPayment_WhenPaymentIsReady() {
        CreditsV3Entity creditsV3Entity = buildEntity(LocalDate.now().getDayOfMonth());
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsV3Entity));
        when(automaticDebitPaymentService.payment(processPaymentCaptor.capture())).thenReturn(Try.of(() -> ProcessPayment.builder().build()));
        when(coreBankingService.getLoanInformation(anyString(), anyString())).thenReturn(Either.right(readyToPaid()));
        Try<Void> response = makeAutomaticPaymentUseCase.execute(makeAutomaticDebit());
        Mockito.verify(schedulerNotificationAsyncService, times(1)).successNotification(transactionRequestCaptor.capture());
        assertThat(response.isSuccess(), is(true));
        assertProcessPaymentCaptor(processPaymentCaptor.getValue());
        assertTransactionRequest(transactionRequestCaptor.getValue());
    }


    @Test
    public void automaticPayment_WhenPaymentIsPaid() {
        CreditsV3Entity creditsV3Entity = buildEntity(LocalDate.now().getDayOfMonth());
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsV3Entity));
        when(coreBankingService.getLoanInformation(anyString(), anyString())).thenReturn(Either.right(paid()));
        Try<Void> response = makeAutomaticPaymentUseCase.execute(makeAutomaticDebit());
        assertThat(response.isSuccess(), is(true));
        Mockito.verify(schedulerNotificationAsyncService, times(1)).successNotification(transactionRequestCaptor.capture());
        Mockito.verify(automaticDebitPaymentService, never()).payment(any());
        assertTransactionRequest(transactionRequestCaptor.getValue());
    }

    @Test
    public void automaticPayment_WhenLoanIsNotActive() {
        CreditsV3Entity creditsV3Entity = buildEntity(LocalDate.now().getDayOfMonth());
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsV3Entity));
        when(coreBankingService.getLoanInformation(anyString(), anyString())).thenReturn(Either.right(inArrears()));
        Try<Void> response = makeAutomaticPaymentUseCase.execute(makeAutomaticDebit());
        assertThat(response.isSuccess(), is(true));
        Mockito.verify(schedulerNotificationAsyncService, times(1)).failedNotification(transactionRequestCaptor.capture());
        Mockito.verify(schedulerAsyncService, times(1)).retryTransaction(transactionRequestCaptor.capture());
        Mockito.verify(automaticDebitPaymentService, never()).payment(any());
        assertTransactionRequest(transactionRequestCaptor.getValue());
    }

    @Test
    public void automaticPayment_WhenPaymentDate_not_is_Today() {
        CreditsV3Entity creditsV3Entity = buildEntity(LocalDate.now().getDayOfMonth());
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsV3Entity));
        when(coreBankingService.getLoanInformation(anyString(), anyString())).thenReturn(Either.right(notIsToday()));
        Try<Void> response = makeAutomaticPaymentUseCase.execute(makeAutomaticDebit());
        assertThat(response.isSuccess(), is(true));
        Mockito.verify(schedulerAsyncService, times(1)).oneTimeNotification(createTransactionRequestCaptor.capture());
        Mockito.verify(automaticDebitPaymentService, never()).payment(any());
        assertOneTimeTransaction(createTransactionRequestCaptor.getValue(), LocalDate.now().plusDays(2).getDayOfMonth());
    }

    @Test
    public void automaticPayment_WhenLoanIsActiveInArrears() {
        CreditsV3Entity creditsV3Entity = buildEntity(LocalDate.now().getDayOfMonth());
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsV3Entity));
        when(automaticDebitPaymentService.payment(processPaymentCaptor.capture())).thenReturn(Try.of(() -> ProcessPayment.builder().build()));
        when(coreBankingService.getLoanInformation(anyString(), anyString())).thenReturn(Either.right(activeArrears()));
        Try<Void> response = makeAutomaticPaymentUseCase.execute(makeAutomaticDebit());
        assertThat(response.isSuccess(), is(true));
        Mockito.verify(schedulerNotificationAsyncService, times(1)).successNotification(transactionRequestCaptor.capture());
        Mockito.verify(automaticDebitPaymentService, times(1)).payment(any());
    }

    @Test
    public void automaticPayment_WhenLoanIsActiveInArrearsFailed() {
        CreditsV3Entity creditsV3Entity = buildEntity(LocalDate.now().getDayOfMonth());
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsV3Entity));
        when(automaticDebitPaymentService.payment(processPaymentCaptor.capture())).thenReturn(Try.failure(new RuntimeException("error payment")));
        when(coreBankingService.getLoanInformation(anyString(), anyString())).thenReturn(Either.right(activeArrears()));
        Try<Void> response = makeAutomaticPaymentUseCase.execute(makeAutomaticDebit());
        assertThat(response.isSuccess(), is(true));
        Mockito.verify(automaticDebitPaymentService, times(1)).payment(any());
    }


    @Test
    public void automaticPayment_WhenNone() {
        CreditsV3Entity creditsV3Entity = buildEntity(LocalDate.now().getDayOfMonth());
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsV3Entity));
        LoanInformation loanInformation = LoanInformation.builder()
                .installmentDate(LocalDateTime.now().minusDays(1))
                .installmentExpectedDue(AmountCurrency.builder().value(TEN).build())
                .state("ACTIVE")
                .build();
        when(coreBankingService.getLoanInformation(anyString(), anyString())).thenReturn(Either.right(loanInformation));
        Try<Void> response = makeAutomaticPaymentUseCase.execute(makeAutomaticDebit());
        assertThat(response.isSuccess(), is(true));
        Mockito.verify(schedulerNotificationAsyncService, times(1)).failedNotification(transactionRequestCaptor.capture());
        Mockito.verify(automaticDebitPaymentService, never()).payment(any());
        assertTransactionRequest(transactionRequestCaptor.getValue());
    }

    private void assertOneTimeTransaction(CreateTransactionRequest createTransactionRequest, int dayOneTime) {
        assertThat(createTransactionRequest.getDayOfPay(), is(dayOneTime));
        assertThat(createTransactionRequest.getIdClient(), is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
        assertThat(createTransactionRequest.getIdCredit(), is("014176ef-e291-4db6-9b49-18d253a8ae5d"));
        assertThat(createTransactionRequest.getMetadata(), is(String.valueOf(dayOneTime).concat("#credits#ONE_TIME")));
    }

    private void assertTransactionRequest(TransactionRequest transactionRequest) {
        assertThat(transactionRequest.getDayOfPay(), is(LocalDate.now().getDayOfMonth()));
        assertThat(transactionRequest.getIdClient(), is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
        assertThat(transactionRequest.getMetadata(), is("metadata"));
    }

    private void assertProcessPaymentCaptor(ProcessPayment processPayment) {
        assertThat(processPayment.getLoanInformation().getLoanId(), is("loan_id"));
        assertThat(processPayment.getLoanInformation().getInstallmentExpectedDue().getValue(), is(TEN));
        assertThat(processPayment.getLoanInformation().getInstallmentDate().toLocalDate(), is(LocalDate.now()));
    }

    private MakeAutomaticDebitCommand makeAutomaticDebit() {
        return new MakeAutomaticDebitCommand(UUID.randomUUID().toString(), "metadata");
    }

    private CreditsV3Entity buildEntity(int dayOfPay) {
        CreditsV3Entity creditsV3Entity = creditsEntityWithAcceptOffer();
        creditsV3Entity.setDayOfPay(dayOfPay);
        return creditsV3Entity;
    }
}
