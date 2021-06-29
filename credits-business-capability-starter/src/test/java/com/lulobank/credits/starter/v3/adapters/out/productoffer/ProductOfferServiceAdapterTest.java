package com.lulobank.credits.starter.v3.adapters.out.productoffer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest;
import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest.ProductOfferStatus;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;

public class ProductOfferServiceAdapterTest {

	private static final String CLIENTS_PRODUCT_OFFER_RESOURCE = "clients/api/v1/client/%s/product-offers";

	private ProductOfferServiceAdapter subject;

	@Mock
	private RestTemplateClient clientsRestTemplateClient;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new ProductOfferServiceAdapter(clientsRestTemplateClient);
	}

	@Test
	public void getClientInformationShouldReturnRight() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		ProductOfferRequest productOfferRequest = buildProductOfferRequest(idClient);
		String resource = String.format(CLIENTS_PRODUCT_OFFER_RESOURCE, idClient);
		
		when(clientsRestTemplateClient.put(eq(resource), any(), eq(auth), eq(Void.class)))
				.thenReturn(Either.right(new ResponseEntity<>(HttpStatus.ACCEPTED)));
		Either<UseCaseResponseError, ProductOfferRequest> response = subject.updateProductOffer(productOfferRequest, auth);
		
		assertTrue(response.isRight());
		assertThat(response.get().getIdProductOffer(), is(productOfferRequest.getIdProductOffer()));
		assertThat(response.get().getIdClient(), is(productOfferRequest.getIdClient()));
		assertThat(response.get().getStatus(), is(productOfferRequest.getStatus()));
	}
	
	@Test
	public void getClientInformationShouldReturnLeft() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		ProductOfferRequest productOfferRequest = buildProductOfferRequest(idClient);
		String resource = String.format(CLIENTS_PRODUCT_OFFER_RESOURCE, idClient);
		when(clientsRestTemplateClient.put(eq(resource), any(), eq(auth), eq(Void.class)))
				.thenReturn(Either.left(new HttpError("500", "Unxepected error trying to consume rest client", null)));
		Either<UseCaseResponseError, ProductOfferRequest> response = subject.updateProductOffer(productOfferRequest, auth);
		
		assertTrue("Not found error", response.isLeft());
	}
	
	private ProductOfferRequest buildProductOfferRequest(String idClient) {
		ProductOfferRequest productOfferRequest = new ProductOfferRequest();
		productOfferRequest.setIdClient(idClient);
		productOfferRequest.setIdProductOffer("idProductOffer");
		productOfferRequest.setStatus(ProductOfferStatus.CLOSED);
		return productOfferRequest;
	}
}
