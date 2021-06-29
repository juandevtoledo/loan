package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.v3.port.in.rescheduledloan.RescheduledLoanMessage;
import com.lulobank.credits.v3.port.in.rescheduledloan.RescheduledLoanUseCase;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.credits.starter.v3.util.EntitiesFactory.RescheduledLoanEventFactory;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RescheduledLoanEventHandlerTest {

    @Mock
    private RescheduledLoanUseCase rescheduledLoanUseCase;

    private RescheduledLoanEventHandler rescheduledLoanEventHandler;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        rescheduledLoanEventHandler = new RescheduledLoanEventHandler(rescheduledLoanUseCase);
    }

    @Test
    public void processRescheduledEvent_WhenSuccess() {
        RescheduledLoanMessage rescheduledLoanMessage = RescheduledLoanEventFactory.ok();
        when(rescheduledLoanUseCase.execute(any())).thenReturn(Try.success(null));

        Try<Void> response = rescheduledLoanEventHandler.execute(rescheduledLoanMessage);

        assertTrue(response.isSuccess());
    }

    @Test
    public void processRescheduledEvent_WhenFail() {
        RescheduledLoanMessage rescheduledLoanMessage = RescheduledLoanEventFactory.ok();
        when(rescheduledLoanUseCase.execute(any())).thenReturn(Try.failure(new RuntimeException("Error...")));

        Try<Void> response = rescheduledLoanEventHandler.execute(rescheduledLoanMessage);

        assertTrue(response.isFailure());
    }
}
