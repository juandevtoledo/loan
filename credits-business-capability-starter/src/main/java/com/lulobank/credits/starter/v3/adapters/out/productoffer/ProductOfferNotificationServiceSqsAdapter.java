package com.lulobank.credits.starter.v3.adapters.out.productoffer;

import com.lulobank.credits.starter.v3.adapters.out.productoffer.dto.CreateProductOfferMessage;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferNotificationService;
import com.lulobank.credits.v3.port.out.productoffer.dto.CreateProductOfferNotificationRequest;
import com.lulobank.events.api.EventFactory;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

@CustomLog
@AllArgsConstructor
public class ProductOfferNotificationServiceSqsAdapter implements ProductOfferNotificationService{

    private final SqsBraveTemplate sqsBraveTemplate;
    private final String clientsV2Queue;
	
	@Override
	public Try<Void> createProductOffer(CreateProductOfferNotificationRequest createProductOfferNotificationRequest) {
		log.info("[ProductOfferNotificationServiceSqsAdapter] createProductOffer()");
		return Try.run(() -> sqsBraveTemplate.convertAndSend(clientsV2Queue,
                EventFactory.ofDefaults(buildCreatedProductOfferMessage(createProductOfferNotificationRequest)).build()));
	}

	private CreateProductOfferMessage buildCreatedProductOfferMessage(
			CreateProductOfferNotificationRequest createProductOfferNotificationRequest) {
		return CreateProductOfferMessage.builder()
				.idClient(createProductOfferNotificationRequest.getIdClient())
				.type(createProductOfferNotificationRequest.getType())
				.value(createProductOfferNotificationRequest.getValue())
				.build();
	}
}
