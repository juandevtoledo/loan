package com.lulobank.credits.v3.port.out.queue;

import com.lulobank.credits.v3.events.CreateStatementMessage;
import io.vavr.control.Try;

public interface NotificationLoanStatement {

    Try<Void> requestLoanStatement(CreateStatementMessage createStatementMessage);

}