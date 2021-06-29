package com.lulobank.credits.services.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingAccountDomain {
    private String name;
    private String accountHolderKey;
    private String accountHolderType;
    private String productTypeKey;
    private String accountType;
    private String accountState;
    private String currencyCode;
    private String idMambu;
    private String encodedKey;


}
