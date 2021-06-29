package com.lulobank.credits.services.events;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreatePreApprovedOfferMessage {
    private String idClient;
    private BigDecimal maxTotalAmount;
}
