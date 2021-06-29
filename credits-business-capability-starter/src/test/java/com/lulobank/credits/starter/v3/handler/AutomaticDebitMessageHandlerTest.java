package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.sqs.event.AutomaticDebitMessage;
import com.lulobank.credits.starter.v3.adapters.in.sqs.handler.AutomaticDebitMessageHandler;
import com.lulobank.credits.v3.usecase.automaticdebit.MakeAutomaticPaymentUseCase;
import com.lulobank.credits.v3.usecase.automaticdebit.command.MakeAutomaticDebitCommand;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class AutomaticDebitMessageHandlerTest {

    public static final String CREDIT_ID = "3db1db74-0238-4053-a5f5-8cc7acb7416b";
    private AutomaticDebitMessageHandler automaticDebitMessageHandler;
    @Mock
    private MakeAutomaticPaymentUseCase makeAutomaticPaymentUseCase;
    @Captor
    private ArgumentCaptor<MakeAutomaticDebitCommand> automaticDebitMessageCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        automaticDebitMessageHandler = new AutomaticDebitMessageHandler(makeAutomaticPaymentUseCase);
    }

    @Test
    public void execute_WhenUseCaseResponseSuccess() {
        when(makeAutomaticPaymentUseCase.execute(automaticDebitMessageCaptor.capture())).thenReturn(Try.run(System.out::println));
        Try<Void> response = automaticDebitMessageHandler.execute(automaticDebitMessage());
        assertThat(response.isSuccess(), is(true));
        assertThat(automaticDebitMessageCaptor.getValue().getIdCredit(),is(CREDIT_ID));
    }

    @Test
    public void eventClass_Test(){
        Class<AutomaticDebitMessage> classEvent = automaticDebitMessageHandler.eventClass();
        assertThat(classEvent.getSimpleName(),is("AutomaticDebitMessage"));
    }

    private AutomaticDebitMessage automaticDebitMessage() {
        AutomaticDebitMessage automaticDebitMessage = new AutomaticDebitMessage();
        automaticDebitMessage.setIdCredit(CREDIT_ID);
        return automaticDebitMessage;
    }
}
