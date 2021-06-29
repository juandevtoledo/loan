package com.lulobank.credits.sdk.dto.initialofferv2;

import lombok.Getter;

@Getter
public class AccountCreated {
    private final String  idCbs;
    private final String  idCbshash;

    public AccountCreated(String idCbs, String idCbshash) {
        this.idCbs = idCbs;
        this.idCbshash = idCbshash;
    }
}
