package com.lulobank.credits.services.features.initialoffer;

public enum OffersTypeEnum {

    FAST_LOAN(12, "Cr\u00E9dito a corto plazo",true),
    COMFORTABLE_LOAN(48, "Cr\u00E9dito con cuotas bajas",true),
    FLEXIBLE_LOAN(null, "Cr\u00E9dito personalizado",false);

    private final Integer installment;
    private final String description;
    private final boolean simulate;

    OffersTypeEnum(Integer installment, String description, boolean simulate) {

        this.installment = installment;
        this.description = description;
        this.simulate=simulate;
    }

    public Integer getInstallment() {
        return installment;
    }

    public String getDescription() {
        return description;
    }

    public boolean getSimulate() {
        return simulate;
    }
}
