package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.v3.port.in.digitalevidence.DigitalEvidenceCreatedMessage;
import com.lulobank.credits.v3.port.in.digitalevidence.DigitalEvidenceCreatedUseCase;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;

public class DigitalEvidenceCreatedEventHandler implements EventHandler<DigitalEvidenceCreatedMessage> {

    private final DigitalEvidenceCreatedUseCase digitalEvidenceCreatedUseCase;

    public DigitalEvidenceCreatedEventHandler(DigitalEvidenceCreatedUseCase digitalEvidenceCreatedUseCase){
        this.digitalEvidenceCreatedUseCase = digitalEvidenceCreatedUseCase;

    }

    @Override
    public Try<Void> execute(DigitalEvidenceCreatedMessage payload) {
        return digitalEvidenceCreatedUseCase.execute(payload);
    }

    @Override
    public Class<DigitalEvidenceCreatedMessage> eventClass() {
        return DigitalEvidenceCreatedMessage.class;
    }
}
