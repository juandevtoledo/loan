package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.starter.v3.adapters.in.sqs.event.CloseLoanByPSETotalPaymentMessage;
import com.lulobank.credits.v3.usecase.closeloan.CloseLoanByExternalPaymentUseCase;
import com.lulobank.credits.v3.usecase.closeloan.command.ClientWithExternalPayment;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class CloseLoanByPSEHandler implements EventHandler<CloseLoanByPSETotalPaymentMessage> {

    private final CloseLoanByExternalPaymentUseCase closeLoanByExternalPaymentUseCase;

    @Override
    public Try<Void> execute(CloseLoanByPSETotalPaymentMessage message) {
        log.info("Process closeLoanByPSe , idClient {} ", message.getIdClient());
        return Try.run(() -> executeUseCase(message))
                .onSuccess(client->log.info("Success processing close loan , idClient {} ",message.getIdClient()))
                .onFailure(error -> log.error("Error Processing  close loan by PSETotalPayment , {} ", error.getMessage(), error));
    }

    private void executeUseCase(CloseLoanByPSETotalPaymentMessage message) throws Throwable {
        closeLoanByExternalPaymentUseCase.execute(new ClientWithExternalPayment(message.getIdClient(), message.getProductTransaction()))
                .toTry()
                .getOrElseThrow(exception -> exception);
    }

    @Override
    public Class<CloseLoanByPSETotalPaymentMessage> eventClass() {
        return CloseLoanByPSETotalPaymentMessage.class;
    }
}
