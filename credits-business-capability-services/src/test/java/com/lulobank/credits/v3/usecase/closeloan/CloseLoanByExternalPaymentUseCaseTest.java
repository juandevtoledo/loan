package com.lulobank.credits.v3.usecase.closeloan;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.PseAsyncService;
import com.lulobank.credits.v3.service.CloseLoanService;
import com.lulobank.credits.v3.usecase.closeloan.command.ClientWithExternalPayment;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static com.lulobank.credits.v3.port.out.corebanking.CoreBankingErrorStatus.CRE_110;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CloseLoanByExternalPaymentUseCaseTest {

    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private CloseLoanService closeLoanService;
    @Mock
    private PseAsyncService pseAsyncService;
    @Captor
    private ArgumentCaptor<String> idClientCaptor;
    @Captor
    private ArgumentCaptor<String> productTypeCaptor;
    private final String idClient = "cfe4053d-9f55-40dd-98cc-6ee8a34cac43";
    private final String PRODUCT_TRANSACTION = "PAYMENT_TOTAL";
    private CloseLoanByExternalPaymentUseCase closeLoanByExternalPaymentUseCase;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        closeLoanByExternalPaymentUseCase = new CloseLoanByExternalPaymentUseCase(creditsV3Repository, closeLoanService, pseAsyncService);
    }

    @Test
    public void closeLoan_WhenLoanExistAndActive() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(closeLoanService.close(anyString(), anyString())).thenReturn(Either.right(creditsEntityWithAcceptOffer()));
        when(pseAsyncService.loanClosed(idClientCaptor.capture(), productTypeCaptor.capture())).thenReturn(Try.run(System.out::println));
        Either<UseCaseResponseError, String> response = closeLoanByExternalPaymentUseCase.execute(new ClientWithExternalPayment(idClient,PRODUCT_TRANSACTION));
        assertThat(response.isRight(), is(true));
        assertThat(response.get(), is(idClient));
        assertThat(idClientCaptor.getValue(),is(idClient));
        assertThat(productTypeCaptor.getValue(),is("PAYMENT_TOTAL"));
        Mockito.verify(pseAsyncService,times(1)).loanClosed(anyString(),anyString());
    }

    @Test
    public void closeLoan_WhenLoanIsNotActive() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.none());
        Either<UseCaseResponseError, String> response = closeLoanByExternalPaymentUseCase.execute(new ClientWithExternalPayment(idClient, PRODUCT_TRANSACTION));
        assertThat(response.isRight(), is(true));
        assertThat(response.get(), is(idClient));
        Mockito.verify(closeLoanService, never()).close(anyString(), anyString());
        Mockito.verify(pseAsyncService,never()).loanClosed(anyString(),anyString());
    }

    @Test
    public void closeLoan_WhenCloseLoanFailed() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(closeLoanService.close(anyString(), anyString())).thenReturn(Either.left(CreditsError.persistError()));
        Either<UseCaseResponseError, String> response = closeLoanByExternalPaymentUseCase.execute(new ClientWithExternalPayment(idClient, PRODUCT_TRANSACTION));
        assertThat(response.isLeft(), is(true));
        assertThat(response.getLeft().getBusinessCode(), is(CRE_110.name()));
        Mockito.verify(pseAsyncService,never()).loanClosed(anyString(),anyString());
    }
}
