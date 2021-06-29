package com.lulobank.credits.v3.usecase.movements;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.GetMovementsRequest;
import com.lulobank.credits.v3.usecase.movement.LoanMovementsUseCase;
import com.lulobank.credits.v3.usecase.movement.dto.Movement;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.buildLoanMovements;
import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.buildLoanMovementsForFiltering;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static com.lulobank.credits.v3.util.EntitiesFactory.LoanFactory;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoanMovementsUseCaseTest {

    private LoanMovementsUseCase loanMovementsUseCase;
    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private CoreBankingService coreBankingService;
    @Captor
    private ArgumentCaptor<GetMovementsRequest> getMovementsRequestCaptor;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);
        loanMovementsUseCase = new LoanMovementsUseCase(creditsV3Repository, coreBankingService);
    }

    @Test
    public void getLoanMovements_WhenCreditExist() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(
                Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanMovements(getMovementsRequestCaptor.capture())).thenReturn(
                Either.right(buildLoanMovements()));

        Either<UseCaseResponseError, List<Movement>> response = loanMovementsUseCase.execute(
                LoanFactory.createGetMovements());

        assertTrue(response.isRight());
        assertThatLoanMovements(response.get(), 5);
    }

    @Test
    public void getLoanMovementsFiltered_WhenCreditExist() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(
                Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanMovements(getMovementsRequestCaptor.capture())).thenReturn(
                Either.right(buildLoanMovementsForFiltering()));

        Either<UseCaseResponseError, List<Movement>> response = loanMovementsUseCase.execute(
                LoanFactory.createGetMovements());

        assertTrue(response.isRight());
        assertThatLoanMovements(response.get(), 5);
    }

    @Test
    public void getLoanMovements_WhenCreditNotExist() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.none());

        Either<UseCaseResponseError, List<Movement>> response = loanMovementsUseCase.execute(
                LoanFactory.createGetMovements());

        assertTrue(response.isLeft());
        assertThat(response.getLeft().getDetail(), is("D"));
        assertThat(response.getLeft().getProviderCode(), is("404"));
        assertThat(response.getLeft().getBusinessCode(), is("CRE_101"));
        verify(coreBankingService, never()).getLoanMovements(any());
    }

    @Test
    public void getLoanMovements_WhenCoreBankingError() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(
                Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanMovements(any())).thenReturn(
                Either.left(CoreBankingError.clientWithOutAccountsError()));

        Either<UseCaseResponseError, List<Movement>> response = loanMovementsUseCase.execute(
                LoanFactory.createGetMovements());

        assertTrue(response.isLeft());
        assertThat(response.getLeft().getDetail(), is("P_CB"));
        assertThat(response.getLeft().getProviderCode(), is("502"));
        assertThat(response.getLeft().getBusinessCode(), is("CRE_107"));
    }

    private void assertThatLoanMovements(List<Movement> movements, Integer expectedSize) {
        assertThat(movements.size(), is(expectedSize));
        assertThat(getMovementsRequestCaptor.getValue().getClientId(), is("1999368732"));
        assertThat(getMovementsRequestCaptor.getValue().getLoanNumber(), is("YAMW127"));

    }
}
