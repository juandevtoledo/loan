package com.lulobank.credits.v3.port.out.productoffer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOfferRequest {
	private String idProductOffer;
	private String idClient;
	private ProductOfferStatus status;
	
	public enum ProductOfferStatus {
		CLOSED;
	}
}


