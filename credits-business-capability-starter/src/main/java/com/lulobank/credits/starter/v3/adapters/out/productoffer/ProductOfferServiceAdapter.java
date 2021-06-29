package com.lulobank.credits.starter.v3.adapters.out.productoffer;

import java.util.Map;

import com.lulobank.credits.starter.v3.adapters.out.productoffer.dto.UpdateProductOfferRequest;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferError;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferService;
import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;
import lombok.CustomLog;

@CustomLog
public class ProductOfferServiceAdapter implements ProductOfferService {

	private static final String CLIENTS_PRODUCT_OFFER_RESOURCE = "clients/api/v1/client/%s/product-offers";

	private final RestTemplateClient clientsRestTemplateClient;

	public ProductOfferServiceAdapter(RestTemplateClient clientsRestTemplateClient) {
		this.clientsRestTemplateClient = clientsRestTemplateClient;
	}

	@Override
	public Either<UseCaseResponseError, ProductOfferRequest> updateProductOffer(ProductOfferRequest productOfferRequest,
			Map<String, String> auth) {
		
		log.info("[ProductOfferServiceAdapter] updateProductOffer()");
		String context = String.format(CLIENTS_PRODUCT_OFFER_RESOURCE, productOfferRequest.getIdClient());

		return clientsRestTemplateClient
				.put(context, buildUpdateProductOfferRequest(productOfferRequest), auth, Void.class)
				.peekLeft(error -> log.error(String.format("Error getting data from updateProductOffer(): %s", error.getBody())))
				.map(v -> productOfferRequest)
				.mapLeft(error -> ProductOfferError.errorGettingData());
	}

	private UpdateProductOfferRequest buildUpdateProductOfferRequest(ProductOfferRequest productOfferRequest) {
		UpdateProductOfferRequest updateProductOfferRequest = new UpdateProductOfferRequest();
		updateProductOfferRequest.setIdProductOffer(productOfferRequest.getIdProductOffer());
		updateProductOfferRequest.setStatus(productOfferRequest.getStatus().name());
		return updateProductOfferRequest;
	}

}
