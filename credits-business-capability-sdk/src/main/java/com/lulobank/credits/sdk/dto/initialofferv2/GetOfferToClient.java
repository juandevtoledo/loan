package com.lulobank.credits.sdk.dto.initialofferv2;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class GetOfferToClient implements Command {


    private String idClient;
    @NotNull(message = "ClientLoanRequestedAmount is null or empty")
    private Double clientLoanRequestedAmount;
    private String loanPurpose;
    @NotNull(message = "RiskEngineAnalysis is null")
    private RiskEngineAnalysis riskEngineAnalysis;
    @NotNull(message = "ClientInformation is null")
    private ClientInformation clientInformation;


}
