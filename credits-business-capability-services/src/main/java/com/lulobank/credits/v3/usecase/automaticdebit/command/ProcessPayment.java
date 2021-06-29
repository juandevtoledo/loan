package com.lulobank.credits.v3.usecase.automaticdebit.command;

import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import io.vavr.control.Option;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessPayment {
    private final String idClient;
    private final String idCredit;
    private final int dayOfPay;
    private final String idLoanAccountMambu;
    private final String idCoreBanking;
    private final LoanInformation loanInformation;
    private final String metadataEvent;


    public boolean isOneTime() {
        return Option.of(metadataEvent)
                .map(metadata -> metadata.contains("ONE_TIME"))
                .getOrElse(false);
    }
}
