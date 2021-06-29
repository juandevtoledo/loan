package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ApprovedProductOffer {
    private final String idOffer;
    private final Double amount;
    private final Float interestRate;
    private final Integer installments;
    private final Double amountInstallment;
    private final Double insuranceCost;
    private final String type;
    private final String name;
    private final Float monthlyNominalRate;
    private final List<OfferInstallment> simulateInstallment;
}
