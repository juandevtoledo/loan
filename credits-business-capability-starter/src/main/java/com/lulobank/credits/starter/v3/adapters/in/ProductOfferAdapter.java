package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferRequest;
import com.lulobank.credits.starter.v3.handler.ProductOfferHandler;
import io.vavr.collection.Iterator;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.lulobank.credits.v3.util.HttpDomainStatus.BAD_REQUEST;
import static com.lulobank.credits.v3.util.HttpDomainStatus.INTERNAL_SERVER_ERROR;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_100;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_104;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.UNKNOWN_DETAIL;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.VALIDATION_DETAIL;

@CustomLog
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/client/{idClient}/product-offers/purpose")
@RequiredArgsConstructor
public class ProductOfferAdapter {

    private final ProductOfferHandler productOfferHandler;

    @PutMapping
    @Deprecated
    public ResponseEntity<AdapterResponse> getApprovedOffers(@RequestHeader final HttpHeaders headers,
                                                             @PathVariable("idClient") String idClient,
                                                             @Valid @RequestBody final ProductOfferRequest request) {
        return productOfferHandler.generateProductOffer(request, idClient, headers);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        return Iterator.ofAll(ex.getBindingResult().getAllErrors())
                .peek(error -> log.error("Validation error: {}", error.getDefaultMessage()))
                .map(error -> new ErrorResponse(String.valueOf(BAD_REQUEST.value()), CRE_104.name(), VALIDATION_DETAIL))
                .getOrElse(new ErrorResponse(String.valueOf(INTERNAL_SERVER_ERROR.value()), CRE_100.name(), UNKNOWN_DETAIL));
    }
}
