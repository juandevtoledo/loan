package com.lulobank.credits.v3.port.in.productoffer.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Offer {
    private final String idOffer;
    private final Double amount;
    private final Float interestRate;
    private final Integer installments;
    private final Double amountInstallment;
    private final Double insuranceCost;
    private final String type;
    private final String name;
    private final Float monthlyNominalRate;
    private final List<SimulatedInstallment> simulateInstallment;
}
