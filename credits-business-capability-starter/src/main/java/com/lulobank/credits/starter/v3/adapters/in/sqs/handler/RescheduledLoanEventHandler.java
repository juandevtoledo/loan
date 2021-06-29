package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.v3.port.in.rescheduledloan.RescheduledLoanUseCase;
import com.lulobank.credits.v3.port.in.rescheduledloan.RescheduledLoanMessage;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;

public class RescheduledLoanEventHandler implements EventHandler<RescheduledLoanMessage> {

    private final RescheduledLoanUseCase rescheduledLoanUseCase;

    public RescheduledLoanEventHandler(RescheduledLoanUseCase rescheduledLoanUseCase) {
        this.rescheduledLoanUseCase = rescheduledLoanUseCase;
    }

    @Override
    public Try<Void> execute(RescheduledLoanMessage payload) {
        return rescheduledLoanUseCase.execute(payload);
    }

    @Override
    public Class<RescheduledLoanMessage> eventClass() {
        return RescheduledLoanMessage.class;
    }
}
