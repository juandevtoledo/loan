package com.lulobank.credits.starter.v3.adapters.out.productoffer;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.credits.v3.port.out.productoffer.dto.CreateProductOfferNotificationRequest;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import io.vavr.control.Try;

public class ProductOfferNotificationServiceSqsAdapterTest {
	
	private ProductOfferNotificationServiceSqsAdapter productOfferNotificationServiceSqsAdapter;
	
	@Mock
	private SqsBraveTemplate sqsBraveTemplate;
	
	private String clientsV2Queue;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		clientsV2Queue = "clientsV2Queue";
		productOfferNotificationServiceSqsAdapter = new ProductOfferNotificationServiceSqsAdapter(sqsBraveTemplate, clientsV2Queue);
	}
	
	@Test
	public void createProductOfferSuccess() {
		CreateProductOfferNotificationRequest productOfferNotificationRequest = buildCreateProductOfferNotificationRequest();
		Try<Void> response = productOfferNotificationServiceSqsAdapter.createProductOffer(productOfferNotificationRequest);
		assertTrue(response.isSuccess());
		verify(sqsBraveTemplate).convertAndSend(eq(clientsV2Queue), any());
		
	}

	private CreateProductOfferNotificationRequest buildCreateProductOfferNotificationRequest() {
		return CreateProductOfferNotificationRequest.builder()
				.idClient("idClient")
				.type("type")
				.value(10)
				.build();
	}

}
