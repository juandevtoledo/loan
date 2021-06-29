package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.starter.v3.adapters.in.sqs.event.AutomaticDebitMessage;
import com.lulobank.credits.v3.usecase.automaticdebit.MakeAutomaticPaymentUseCase;
import com.lulobank.credits.v3.usecase.automaticdebit.command.MakeAutomaticDebitCommand;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutomaticDebitMessageHandler implements EventHandler<AutomaticDebitMessage> {

    private final MakeAutomaticPaymentUseCase makeAutomaticPaymentUseCase;

    @Override
    public Try<Void> execute(AutomaticDebitMessage automaticDebitMessage) {
        return makeAutomaticPaymentUseCase.execute(getCommand(automaticDebitMessage));
    }

    private MakeAutomaticDebitCommand getCommand(AutomaticDebitMessage automaticDebitMessage) {
        return new MakeAutomaticDebitCommand(automaticDebitMessage.getIdCredit(), automaticDebitMessage.getMetadata());
    }

    @Override
    public Class<AutomaticDebitMessage> eventClass() {
        return AutomaticDebitMessage.class;
    }
}
