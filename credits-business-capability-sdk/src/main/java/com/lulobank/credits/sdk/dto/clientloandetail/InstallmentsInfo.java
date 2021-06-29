package com.lulobank.credits.sdk.dto.clientloandetail;

import lombok.Getter;

@Getter
public class InstallmentsInfo {

    private final Integer installments;
    private final Integer paidInstallments;
    private final Integer dueInstallments;

    public InstallmentsInfo(Integer installments , Integer paidInstallments, Integer dueInstallments) {
        this.installments=installments;
        this.paidInstallments = paidInstallments;
        this.dueInstallments = dueInstallments;
    }
}
