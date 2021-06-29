package com.lulobank.credits.starter.v3.adapters.out.sqs;

import com.lulobank.credits.services.events.NewReportEvent;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;

public class ReportingV3Sqs extends SqsV3Integration<LoanTransaction, NewReportEvent> {


    public ReportingV3Sqs(String endpoint) {
        super(endpoint);
    }

    @Override
    public Event<NewReportEvent> map(LoanTransaction loanTransaction) {
        NewReportEvent newReportEvent = new NewReportEvent();
        newReportEvent.setIdClient(loanTransaction.getEntity().getIdClient());
        newReportEvent.setIdProduct(loanTransaction.getEntity().getIdClientMambu());
        newReportEvent.setTypeReport(TypeReport.CREDIT_CONTRACT);
        return EventFactory.ofDefaults(newReportEvent).build();
    }
}
