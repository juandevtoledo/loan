package com.lulobank.credits.services.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisbursementDomain {
    private TransferDetailsDomain transferDetailsDomain;
    private String originalCurrencyCode;

}
