package com.lulobank.credits.sdk.dto.acceptoffer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Accepted {
    private String idCredit;

    public Accepted(){
    }
    public Accepted(String idCredit) {
        this.idCredit = idCredit;
    }
}
