package com.lulobank.credits.v3.usecase.intialsoffersv3.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetOffersByClient {
    private final String idClient;
    private final Double clientLoanRequestedAmount;
    private final String loanPurpose;
    private final RiskEngineAnalysisV3 riskEngineAnalysis;
    private final ClientInformationV3 clientInformation;
}
