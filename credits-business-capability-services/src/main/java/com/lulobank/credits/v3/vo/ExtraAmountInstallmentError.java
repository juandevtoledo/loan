package com.lulobank.credits.v3.vo;

import static com.lulobank.credits.v3.util.HttpDomainStatus.INTERNAL_SERVER_ERROR;

public class ExtraAmountInstallmentError extends UseCaseResponseError  {

    public ExtraAmountInstallmentError(CreditsErrorStatus creditsErrorStatus, String providerCode) {
        super(creditsErrorStatus.name(), providerCode);
    }

    public ExtraAmountInstallmentError(CreditsErrorStatus creditsErrorStatus, String providerCode, String detail) {
        super(creditsErrorStatus.name(), providerCode, detail);
    }

    public static ExtraAmountInstallmentError errorExtraPayment() {
        return new ExtraAmountInstallmentError(CreditsErrorStatus.CRE_105, String.valueOf(INTERNAL_SERVER_ERROR.value()), CreditsErrorStatus.VALIDATION_DETAIL);
    }
}
