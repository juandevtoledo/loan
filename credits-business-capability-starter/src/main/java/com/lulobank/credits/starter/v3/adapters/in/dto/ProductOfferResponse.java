package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProductOfferResponse extends AdapterResponse {
    private final Double amount;
    private final String riskModelResponse;
    private final String idCredit;
    private final List<ApprovedProductOffer> offers;
    private final LocalDateTime currentDate;
}