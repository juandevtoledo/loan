package com.lulobank.credits.v3.usecase.preappoveloanoffers.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class Offer {
    private final String idOffer;
    private final BigDecimal amount;
    private final Double insuranceCost;
    private final String type;
    private final String name;
    private final List<SimulationInstallment> simulateInstallment;
}
