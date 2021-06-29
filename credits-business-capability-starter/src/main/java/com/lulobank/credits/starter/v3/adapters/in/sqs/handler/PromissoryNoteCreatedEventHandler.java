package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.v3.port.in.promissorynote.CreatePromissoryNoteResponseMessage;
import com.lulobank.credits.v3.port.in.promissorynote.PromissoryNoteCreatedUseCase;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;

public class PromissoryNoteCreatedEventHandler implements EventHandler<CreatePromissoryNoteResponseMessage> {

    private final PromissoryNoteCreatedUseCase promissoryNoteCreatedUseCase;

    public PromissoryNoteCreatedEventHandler(PromissoryNoteCreatedUseCase promissoryNoteCreatedUseCase){
        this.promissoryNoteCreatedUseCase = promissoryNoteCreatedUseCase;

    }

    @Override
    public Try<Void> execute(CreatePromissoryNoteResponseMessage createPromissoryNoteResponseMessage) {
        return promissoryNoteCreatedUseCase.execute(createPromissoryNoteResponseMessage);
    }

    @Override
    public Class<CreatePromissoryNoteResponseMessage> eventClass() {
        return CreatePromissoryNoteResponseMessage.class;
    }
}
