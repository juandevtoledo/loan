package com.lulobank.credits.v3.port.out.productoffer;

import com.lulobank.credits.v3.port.out.productoffer.dto.CreateProductOfferNotificationRequest;

import io.vavr.control.Try;

public interface ProductOfferNotificationService {

    Try<Void> createProductOffer(CreateProductOfferNotificationRequest createProductOfferNotificationRequest);
}
