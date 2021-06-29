package com.lulobank.credits.starter.v3.adapters.in.acceptoffer.dto;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AcceptOfferResponse extends AdapterResponse {
	private boolean valid;
}
