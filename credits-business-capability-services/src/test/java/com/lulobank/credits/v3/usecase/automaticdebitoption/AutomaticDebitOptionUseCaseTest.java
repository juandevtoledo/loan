package com.lulobank.credits.v3.usecase.automaticdebitoption;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.CreateTransactionRequest;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;
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

import static com.lulobank.credits.v3.util.EntitiesFactory.AutomaticDebitFactory.updateAutomaticDebitOption;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityDisabledAutomaticDebit;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AutomaticDebitOptionUseCaseTest {

    private AutomaticDebitOptionUseCase automaticDebitOptionUseCase;
    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private SchedulerTransactionAsyncService schedulerTransactionAsyncService;
    @Captor
    private ArgumentCaptor<CreateTransactionRequest> createTransactionRequestCaptor;
    @Captor
    private ArgumentCaptor<TransactionRequest> transactionRequestCaptor;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);
        automaticDebitOptionUseCase = new AutomaticDebitOptionUseCase(creditsV3Repository,
                schedulerTransactionAsyncService);
    }

    @Test
    public void updateAutomaticDebitOptionToTrue_WhenCreditExist() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(
                Option.of(creditsEntityDisabledAutomaticDebit()));
        when(creditsV3Repository.save(any())).thenReturn(
                Try.success(creditsEntityWithAcceptOffer()));

        Either<UseCaseResponseError, Boolean> response = automaticDebitOptionUseCase.execute(
                updateAutomaticDebitOption(true));

        assertTrue(response.isRight());
        assertTrue(response.get());
        verify(schedulerTransactionAsyncService, times(1)).createTransaction(createTransactionRequestCaptor.capture());
        assertCreateTransactionRequest(createTransactionRequestCaptor.getValue());
    }


    @Test
    public void updateAutomaticDebitOptionToFalse_WhenCreditExist() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(
                Option.of(creditsEntityWithAcceptOffer()));
        when(creditsV3Repository.save(any())).thenReturn(
                Try.success(creditsEntityDisabledAutomaticDebit()));

        Either<UseCaseResponseError, Boolean> response = automaticDebitOptionUseCase.execute(
                updateAutomaticDebitOption(false));

        assertTrue(response.isRight());
        assertTrue(response.get());
        verify(schedulerTransactionAsyncService, times(1)).deleteTransaction(transactionRequestCaptor.capture());
        assertDeleteTransactionRequest(transactionRequestCaptor.getValue());
    }


    @Test
    public void updateAutomaticDebitOption_WhenCreditNotExist() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.none());

        Either<UseCaseResponseError, Boolean> response = automaticDebitOptionUseCase.execute(
                updateAutomaticDebitOption(true));

        assertTrue(response.isLeft());
        assertThat(response.getLeft().getDetail(), is("D"));
        assertThat(response.getLeft().getProviderCode(), is("404"));
        assertThat(response.getLeft().getBusinessCode(), is("CRE_101"));
        verify(creditsV3Repository, never()).save(any());
        verify(schedulerTransactionAsyncService, never()).createTransaction(any());
        verify(schedulerTransactionAsyncService, never()).deleteTransaction(any());
    }

    @Test
    public void getLoanMovements_WhenCoreBankingError() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(
                Option.of(creditsEntityDisabledAutomaticDebit()));
        when(creditsV3Repository.save(any())).thenReturn(
                Try.failure(new RuntimeException()));

        Either<UseCaseResponseError, Boolean> response = automaticDebitOptionUseCase.execute(
                updateAutomaticDebitOption(true));

        assertTrue(response.isLeft());
        assertThat(response.getLeft().getDetail(), is("D"));
        assertThat(response.getLeft().getProviderCode(), is("502"));
        assertThat(response.getLeft().getBusinessCode(), is("CRE_110"));
        verify(schedulerTransactionAsyncService, never()).createTransaction(any());
        verify(schedulerTransactionAsyncService, never()).deleteTransaction(any());
    }

    private void assertCreateTransactionRequest(CreateTransactionRequest createTransactionRequest) {
        assertThat(createTransactionRequest.getMetadata(),is("15#credits#SUBSCRIPTION"));
        assertThat(createTransactionRequest.getIdCredit(),is("014176ef-e291-4db6-9b49-18d253a8ae5d"));
        assertThat(createTransactionRequest.getIdClient(),is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
        assertThat(createTransactionRequest.getDayOfPay(),is(15));
    }

    private void assertDeleteTransactionRequest(TransactionRequest transactionRequest) {
        assertThat(transactionRequest.getMetadata(),is("15#credits#SUBSCRIPTION"));
        assertThat(transactionRequest.getDayOfPay(),is(15));
        assertThat(transactionRequest.getIdClient(),is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
    }
}
