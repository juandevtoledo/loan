package com.lulobank.credits.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YaTransferCreateWallet {

    private String idClient;
    private String idClientCBS;
    private String idSavingAccount;
    private String phonePrefix;
    private String phoneNumber;

}