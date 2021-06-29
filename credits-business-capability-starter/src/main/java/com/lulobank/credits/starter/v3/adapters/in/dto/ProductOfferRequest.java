package com.lulobank.credits.starter.v3.adapters.in.dto;

import com.lulobank.credits.v3.vo.AdapterCredentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOfferRequest {
    @NotNull(message = "idProductOffer is null or empty")
    private String idProductOffer;
    private String loanPurpose;
    @NotNull(message = "amount is null or empty")
    private Double amount;
    private String idClient;
    private AdapterCredentials adapterCredentials;
}
