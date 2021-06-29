package com.lulobank.credits.v3.usecase.productoffer.command;

import com.lulobank.credits.v3.vo.AdapterCredentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateOfferRequest {
    private String idProductOffer;
    private String loanPurpose;
    private Double amount;
    private String idClient;
    private AdapterCredentials adapterCredentials;
}
