package com.lulobank.credits.sdk.dto.loandetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NextInstallment {
    private String dueOn;
    private Double amount;
    public NextInstallment() {
    }
    public NextInstallment(String dueOn, Double amount) {
        this.dueOn = dueOn;
        this.amount = amount;
    }
}
