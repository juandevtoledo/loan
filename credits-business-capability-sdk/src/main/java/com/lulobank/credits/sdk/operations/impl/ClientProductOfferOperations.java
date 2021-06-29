package com.lulobank.credits.sdk.operations.impl;

import com.lulobank.credits.sdk.dto.CreditSuccessResult;
import com.lulobank.credits.sdk.dto.acceptoffer.Accepted;
import com.lulobank.credits.sdk.dto.acceptoffer.CreditWithOffer;
import com.lulobank.credits.sdk.operations.IClientProductOfferOperations;
import com.lulobank.credits.sdk.operations.util.RetrofitsUtil;
import com.lulobank.utils.client.retrofit.RetrofitFactory;
import com.lulobank.utils.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

import java.util.Map;

public class ClientProductOfferOperations implements IClientProductOfferOperations {

    private Retrofit retrofit;
    private ClientProductOfferService service;

    public ClientProductOfferOperations(String url) {
        this.retrofit = RetrofitFactory.buildRetrofit(url);
        service = this.retrofit.create(ClientProductOfferService.class);
    }

    @Override
    public ResponseEntity acceptOffer(Map<String, String> headers,CreditWithOffer request) {


        Call callService = service.acceptOffer(headers, request);
        try {
             Response response = callService.execute();
            return new RetrofitsUtil<>().getResponseEntity(response);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e);
        }
    }

    private interface ClientProductOfferService {
        @POST("credits/products/offer/accept")
        Call<CreditSuccessResult<Accepted>> acceptOffer(@HeaderMap Map<String, String> headers, @Body CreditWithOffer request);
    }
}

