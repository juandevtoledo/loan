package com.lulobank.credits.v3.events;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GoodStandingCertificateEvent {
    private final String idClient;
    private final String typeReport;
    private final String idLoanAccountMambu;
    private final String acceptDate;
    private final String closedDate;
    private final BigDecimal amount;
    private final ClientInformationByIdClient clientInformationByIdClient;

    @Getter
    @Builder
    public static class ClientInformationByIdClient {
        private final Content content;
    }

    @Getter
    @Builder
    public static class Content {
        private final String name;
        private final String lastName;
        private final String idCard;
        private final String emailAddress;
    }
}
