package com.lulobank.credits.sdk.operations;

import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;

import java.util.Map;

public interface InitialOffersOperations {

    boolean initialOffers(Map<String, String> headers, GetOfferToClient getOfferToClient, String idClient);
}
