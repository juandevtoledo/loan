package com.lulobank.credits.starter.v3.adapters.in.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToWaitingRequest {
	
    @NotNull(message = "idProductOffer is null or empty")
    private String idProductOffer;
}
