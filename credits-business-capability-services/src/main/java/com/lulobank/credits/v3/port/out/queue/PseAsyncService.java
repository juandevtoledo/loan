package com.lulobank.credits.v3.port.out.queue;

import io.vavr.control.Try;

public interface PseAsyncService {

    Try<Void> loanClosed(String idClient , String productType );
}
