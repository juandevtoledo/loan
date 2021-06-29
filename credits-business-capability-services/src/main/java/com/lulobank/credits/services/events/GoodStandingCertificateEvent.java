package com.lulobank.credits.services.events;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GoodStandingCertificateEvent {
    private String idClient;
    private String typeReport;
    private String idLoanAccountMambu;
    private String acceptDate;
    private String closedDate;
    private BigDecimal amount;
}
