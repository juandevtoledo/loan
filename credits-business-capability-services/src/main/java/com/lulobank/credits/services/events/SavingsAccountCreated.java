package com.lulobank.credits.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingsAccountCreated {

    private String idClient;
    private String idClientCBS;
    private String idClientCBSHash;
    private String idSavingAccount;
    private String idSavingAccountCBSHash;
}
