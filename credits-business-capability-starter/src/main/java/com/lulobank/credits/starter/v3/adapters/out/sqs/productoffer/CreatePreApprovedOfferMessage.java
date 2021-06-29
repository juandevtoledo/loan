package com.lulobank.credits.starter.v3.adapters.out.sqs.productoffer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePreApprovedOfferMessage {
    private String idClient;
    private BigDecimal maxTotalAmount;
}
