package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.movement.Movement;
import com.lulobank.credits.starter.v3.adapters.in.dto.movement.MovementsResponse;
import com.lulobank.credits.v3.usecase.movement.LoanMovementsUseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static com.lulobank.credits.starter.utils.Constants.LIMIT;
import static com.lulobank.credits.starter.utils.Constants.OFFSET;
import static com.lulobank.credits.starter.v3.util.EntitiesFactory.LoanInformationFactory.getLoanMovements;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

public class LoanMovementsHandlerTest {

    @Mock
    private LoanMovementsUseCase loanMovementsUseCase;
    private LoanMovementsHandler loanMovementsHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loanMovementsHandler = new LoanMovementsHandler(loanMovementsUseCase);
    }

    @Test
    public void getLoanMovements_WhenUseCaseResponseSuccess() {
        when(loanMovementsUseCase.execute(any())).thenReturn(Either.right(getLoanMovements()));
        ResponseEntity<AdapterResponse> responseEntity = loanMovementsHandler.getLoanMovements(
                UUID.randomUUID().toString(), OFFSET, LIMIT);
        assertThat(responseEntity.getStatusCode(), is(OK));
        MovementsResponse movementsResponse = (MovementsResponse) responseEntity.getBody();

        assertThatLoanMovements(movementsResponse.getMovements());
    }

    @Test
    public void getLoanMovements_WhenUseCaseResponseError() {
        when(loanMovementsUseCase.execute(any())).thenReturn(Either.left(CreditsError.databaseError()));
        ResponseEntity<AdapterResponse> responseEntity = loanMovementsHandler.getLoanMovements(
                UUID.randomUUID().toString(), OFFSET, LIMIT);
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse.getCode(), is("CRE_101"));
        assertThat(errorResponse.getDetail(), is("D"));
        assertThat(errorResponse.getFailure(), is("404"));
    }

    private void assertThatLoanMovements(List<Movement> movements) {
        assertThat(movements.size(), is(3));
    }

}
