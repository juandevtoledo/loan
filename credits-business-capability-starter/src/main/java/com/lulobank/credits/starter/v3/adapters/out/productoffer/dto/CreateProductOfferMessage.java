package com.lulobank.credits.starter.v3.adapters.out.productoffer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateProductOfferMessage {
    private String idClient;
    private String type;
    private Integer value;
}
