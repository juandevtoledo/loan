package com.lulobank.credits.sdk.operations.impl;

import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.sdk.exceptions.InitialOffersException;
import com.lulobank.credits.sdk.operations.InitialOffersOperations;
import com.lulobank.utils.client.retrofit.RetrofitFactory;
import io.vavr.control.Option;
import io.vavr.control.Try;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.Map;

import static org.apache.logging.log4j.util.Strings.EMPTY;

public class RetrofitInitialOffersOperations implements InitialOffersOperations {

    private final InitialOffersService service;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int ACCEPTED = 201;
    public static final int RETROFIT_TIME_OUT = 35;

    public RetrofitInitialOffersOperations(String url) {
        Retrofit retrofit = RetrofitFactory.buildRetrofit(url, RETROFIT_TIME_OUT, RETROFIT_TIME_OUT, RETROFIT_TIME_OUT);
        service = retrofit.create(InitialOffersService.class);
    }

    @Override
    public boolean initialOffers(Map<String, String> headers, GetOfferToClient getOfferToClient, String idClient) {
        return Try.of(() -> {
            Call<Void> callService = service.initialOffer(headers, getOfferToClient, idClient);
            return analyzeResponse(callService.execute());
        }).recover(IOException.class, ex -> {
            throw new InitialOffersException(INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
        }).get();
    }

    public boolean analyzeResponse(Response response) {
        return Option.of(response.code() == ACCEPTED)
                .filter(Boolean::booleanValue)
                .getOrElseThrow(() ->
                        new InitialOffersException(response.code(), getErrorBody(response)));
    }

    private String getErrorBody(Response response) {
        return Option.of(response.errorBody())
                .map(e ->
                        Try.of(() -> response.errorBody().string())
                                .recover(ex -> {
                                    throw new InitialOffersException(INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
                                })
                                .get()
                )
                .getOrElse(EMPTY);
    }

    private interface InitialOffersService {

        @POST("credits/products/v2/loan/client/{idClient}/initial-offer")
        Call<Void> initialOffer(@HeaderMap Map<String, String> headers, @Body GetOfferToClient request, @Path("idClient") String idClient);
    }
}
