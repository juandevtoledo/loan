package com.lulobank.credits.v3.usecase.preappoveloanoffers.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class GetOffersByIdClient {
    private final String idClient;
    private final BigDecimal clientLoanRequestedAmount;
}
