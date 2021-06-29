package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.v3.port.in.approvedriskengine.LoanAssessmentResultMessage;
import com.lulobank.credits.v3.port.in.approvedriskengine.LoanAssessmentResultUseCase;
import com.lulobank.events.api.EventHandler;

import io.vavr.control.Try;

public class LoanAssessmentResultHandler implements EventHandler<LoanAssessmentResultMessage> {

    private final LoanAssessmentResultUseCase loanAssessmentResultUseCase;

    public LoanAssessmentResultHandler(LoanAssessmentResultUseCase loanAssessmentResultUseCase) {
        this.loanAssessmentResultUseCase = loanAssessmentResultUseCase;
    }

    @Override
    public Try<Void> execute(LoanAssessmentResultMessage payload) { return loanAssessmentResultUseCase.execute(payload); }

    @Override
    public Class<LoanAssessmentResultMessage> eventClass() { return LoanAssessmentResultMessage.class; }
}
