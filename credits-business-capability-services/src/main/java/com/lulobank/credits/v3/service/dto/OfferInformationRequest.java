package com.lulobank.credits.v3.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OfferInformationRequest {
    private final String idClient;
    private final BigDecimal interestRate;
    private final Double loanAmount;
    private final Double clientLoanRequestedAmount;
    private final Double clientMonthlyAmountCapacity;
    private final Double feeInsurance;
}
