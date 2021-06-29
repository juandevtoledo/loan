package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.sdk.dto.initialofferv2.ClientInformation;
import com.lulobank.credits.sdk.dto.initialofferv2.RiskEngineAnalysis;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class InitialsOffersRequestV3 {

    private String idClient;
    @NotNull(message = "ClientLoanRequestedAmount is null or empty")
    private Double clientLoanRequestedAmount;
    private String loanPurpose;
    @NotNull(message = "RiskEngineAnalysis is null")
    private RiskEngineAnalysis riskEngineAnalysis;
    @NotNull(message = "ClientInformation is null")
    private ClientInformation clientInformation;
}
