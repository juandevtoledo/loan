package com.lulobank.credits.v3.usecase.nextinstallment;

import com.lulobank.credits.v3.port.in.nextinstallment.dto.NextInstallment;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanInArrears;
import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanPaymentIsUpToDate;
import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanPendingApproval;
import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanPendingPayment;
import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanPendingWithLastInstallment;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class NextInstallmentUseCaseTest {

    private NextInstallmentUseCase nextInstallmentUseCase;
    @Mock
    private CoreBankingService coreBankingService;
    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Captor
    private ArgumentCaptor<String> clientIdCaptor;
    @Captor
    private ArgumentCaptor<String> loanIdCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        nextInstallmentUseCase = new NextInstallmentUseCase(coreBankingService, creditsV3Repository);
    }

    @Test
    public void nextInstallment_WhenLoanInArrears() throws IOException {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(clientIdCaptor.capture(), loanIdCaptor.capture())).thenReturn(Either.right(loanInArrears()));
        Either<UseCaseResponseError, NextInstallment> response = nextInstallmentUseCase.execute(UUID.randomUUID().toString());
        assertThat(clientIdCaptor.getValue(), is("YAMW127"));
        assertThat(loanIdCaptor.getValue(), is("1999368732"));
        assertNextInstallmentInArrears(response.get());
        assertThat(response.get().getFlags().isMinimumPaymentActive(), is(false));
        assertThat(response.get().getFlags().isCustomerOweMoney(), is(true));
    }

    @Test
    public void nextInstallment_WhenPaymentIsNext() throws IOException {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(clientIdCaptor.capture(), loanIdCaptor.capture())).thenReturn(Either.right(loanPendingPayment()));
        Either<UseCaseResponseError, NextInstallment> response = nextInstallmentUseCase.execute(UUID.randomUUID().toString());
        assertNextInstallmentNextToPay(response.get());
        assertThat(response.get().getFlags().isMinimumPaymentActive(), is(true));
        assertThat(response.get().getFlags().isCustomerOweMoney(), is(true));
    }
    

    @Test
    public void nextInstallment_WhenPaymentIsUpToDate() throws IOException {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(clientIdCaptor.capture(), loanIdCaptor.capture())).thenReturn(Either.right(loanPaymentIsUpToDate()));
        Either<UseCaseResponseError, NextInstallment> response = nextInstallmentUseCase.execute(UUID.randomUUID().toString());
        assertNextInstallmentUpToDay(response.get());
        assertThat(response.get().getFlags().isCustomerOweMoney(), is(false));
    }

    @Test
    public void nextInstallment_WhenLoanNoExistInRepository() throws IOException {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.none());
        Either<UseCaseResponseError, NextInstallment> response = nextInstallmentUseCase.execute(UUID.randomUUID().toString());
        assertThat(response.isLeft(), is(true));
        UseCaseResponseError useCaseResponseError = response.getLeft();
        assertErrorResponse(useCaseResponseError,"CRE_101","404","D");
    }

    @Test
    public void nextInstallment_WhenCoreBankingError() throws IOException {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(clientIdCaptor.capture(), loanIdCaptor.capture())).thenReturn(Either.left(CoreBankingError.defaultError()));
        Either<UseCaseResponseError, NextInstallment> response = nextInstallmentUseCase.execute(UUID.randomUUID().toString());
        assertThat(response.isLeft(), is(true));
        UseCaseResponseError useCaseResponseError = response.getLeft();
        assertErrorResponse(useCaseResponseError,"CRE_108","502","P_CB");
    }

    @Test
    public void nextInstallment_WhenLoanIsPendingApproval() throws IOException {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(clientIdCaptor.capture(), loanIdCaptor.capture())).thenReturn(Either.right(loanPendingApproval()));
        Either<UseCaseResponseError, NextInstallment> response = nextInstallmentUseCase.execute(UUID.randomUUID().toString());
        assertThat(response.isRight(), is(true));
        assertThat(response.get().getState(), is("PENDING"));
    }

    @Test
    public void nextInstallment_WhenPaymentIsNextAndIsLatest() throws IOException {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(clientIdCaptor.capture(), loanIdCaptor.capture())).thenReturn(Either.right(loanPendingWithLastInstallment()));
        Either<UseCaseResponseError, NextInstallment> response = nextInstallmentUseCase.execute(UUID.randomUUID().toString());
        assertNextInstallmentNextToPay(response.get());
        assertThat(response.get().getFlags().isMinimumPaymentActive(), is(false));
    }



    private void assertErrorResponse(UseCaseResponseError useCaseResponseError, String businessCode, String providerCode, String detail) {
        assertThat(useCaseResponseError.getBusinessCode(), is(businessCode));
        assertThat(useCaseResponseError.getProviderCode(), is(providerCode));
        assertThat(useCaseResponseError.getDetail(), is(detail));
    }

    private void assertNextInstallmentInArrears(NextInstallment nextInstallment) throws IOException {
        assertThat(nextInstallment.getState(), is("ACTIVE_IN_ARREARS"));
        assertThat(nextInstallment.getInstallmentState(), is(InstallmentState.IN_ARREARS));
        assertThat(nextInstallment.getInstallmentDate(), is(LocalDateTime.parse("2020-02-27 10:50", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        assertThat(nextInstallment.getFlags().isPayNow(), is(true));
        assertThat(nextInstallment.getFlags().isAutomaticDebitActive(), is(true));
    }

    private void assertNextInstallmentNextToPay(NextInstallment nextInstallment) throws IOException {
        assertThat(nextInstallment.getState(), is("ACTIVE"));
        assertThat(nextInstallment.getInstallmentState(), is(InstallmentState.NEXT_TO_PAY));
        assertThat(nextInstallment.getInstallmentDate().toLocalDate(), is(LocalDate.now().plusDays(6)));
        assertThat(nextInstallment.getFlags().isPayNow(), is(false));
        assertThat(nextInstallment.getFlags().isAutomaticDebitActive(), is(true));
    }

    private void assertNextInstallmentUpToDay(NextInstallment nextInstallment) throws IOException {
        assertThat(nextInstallment.getState(), is("ACTIVE"));
        assertThat(nextInstallment.getInstallmentState(), is(InstallmentState.UP_TO_DAY));
        assertThat(nextInstallment.getInstallmentDate().toLocalDate(), is(LocalDate.now().plusMonths(1)));
        assertThat(nextInstallment.getFlags().isPayNow(), is(false));
        assertThat(nextInstallment.getFlags().isAutomaticDebitActive(), is(true));
    }
}
