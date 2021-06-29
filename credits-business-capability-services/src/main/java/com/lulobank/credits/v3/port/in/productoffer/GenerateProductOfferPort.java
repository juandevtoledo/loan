package com.lulobank.credits.v3.port.in.productoffer;

import com.lulobank.credits.v3.port.in.productoffer.dto.ProductOffer;
import com.lulobank.credits.v3.usecase.productoffer.command.GenerateOfferRequest;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;

public interface GenerateProductOfferPort extends UseCase<GenerateOfferRequest, Either<UseCaseResponseError, ProductOffer>> {
}
