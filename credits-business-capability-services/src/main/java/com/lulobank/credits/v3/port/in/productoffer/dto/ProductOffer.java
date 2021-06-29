package com.lulobank.credits.v3.port.in.productoffer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductOffer {
    private Double amount;
    private String riskModelResponse;
    private String idCredit;
    private List<Offer> offers;
    private LocalDateTime currentDate;
}