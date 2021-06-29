package com.lulobank.credits.v3.port.out;

import com.lulobank.credits.v3.service.LoanTransaction;

public interface ClientAlertsAsyncService {

    void sendCreditFinishedNotification(LoanTransaction loanTransaction);
}
