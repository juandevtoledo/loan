package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.v3.port.in.clientinformation.UpdateProductEmailMessage;
import com.lulobank.credits.v3.port.in.clientinformation.UpdateProductEmailUseCase;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;

public class UpdateProductEmailEventHandler implements EventHandler<UpdateProductEmailMessage> {

    private final UpdateProductEmailUseCase updateProductEmailUseCase;

    public UpdateProductEmailEventHandler(UpdateProductEmailUseCase updateProductEmailUseCase){
        this.updateProductEmailUseCase = updateProductEmailUseCase;

    }

    @Override
    public Try<Void> execute(UpdateProductEmailMessage payload) {
        return updateProductEmailUseCase.execute(payload);
    }

    @Override
    public Class<UpdateProductEmailMessage> eventClass() {
        return UpdateProductEmailMessage.class;
    }
}
