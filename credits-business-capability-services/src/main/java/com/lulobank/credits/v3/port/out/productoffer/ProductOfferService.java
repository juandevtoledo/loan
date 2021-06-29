package com.lulobank.credits.v3.port.out.productoffer;

import java.util.Map;

import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;

public interface ProductOfferService {

	Either<UseCaseResponseError, ProductOfferRequest> updateProductOffer(ProductOfferRequest productOfferRequest, Map<String,String> auth);
}
