package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.starter.v3.adapters.in.sqs.event.LoanStatementsScheduler;
import com.lulobank.credits.v3.usecase.IntentionLoanStatementUseCase;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;
import lombok.CustomLog;

@CustomLog
public class LoanStatementSchedulerEventHandler implements EventHandler<LoanStatementsScheduler> {

    private final IntentionLoanStatementUseCase intentionLoanStatementUseCase;

    public LoanStatementSchedulerEventHandler(IntentionLoanStatementUseCase intentionLoanStatementUseCase) {
        this.intentionLoanStatementUseCase = intentionLoanStatementUseCase;
    }

    @Override
    public Try<Void> execute(LoanStatementsScheduler s) {
        return Try.run(() -> intentionLoanStatementUseCase.execute(s.getMessage()))
                .onFailure(error->log.error("Error processing LoanStatementsScheduler, msg : {} ",error.getMessage(),error));
    }

    @Override
    public Class<LoanStatementsScheduler> eventClass() {
        return LoanStatementsScheduler.class;
    }
} 