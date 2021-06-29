package com.lulobank.credits.sdk.operations;

import com.lulobank.credits.sdk.dto.CreditSuccessResult;
import com.lulobank.credits.sdk.dto.acceptoffer.Accepted;
import com.lulobank.credits.sdk.dto.acceptoffer.CreditWithOffer;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IClientProductOfferOperations {
    ResponseEntity<CreditSuccessResult<Accepted>> acceptOffer(Map<String, String> headers, CreditWithOffer request);
}
