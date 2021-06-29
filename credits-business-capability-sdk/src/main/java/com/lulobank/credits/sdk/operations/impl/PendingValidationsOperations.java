package com.lulobank.credits.sdk.operations.impl;

import com.lulobank.credits.sdk.dto.ResponsePendingValidations;
import com.lulobank.credits.sdk.operations.IPendingValidationsOperations;
import com.lulobank.utils.client.retrofit.RetrofitFactory;
import com.lulobank.utils.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.Map;

public class PendingValidationsOperations implements IPendingValidationsOperations {

    private Retrofit retrofit;
    private static final String ERROR_MESSAGE = "ERROR";

    public PendingValidationsOperations(String url){
        super();
        this.retrofit = RetrofitFactory.buildRetrofit(url);
    }

    protected PendingValidationsService getPendingValidationsService(){
        return this.retrofit.create(PendingValidationsService.class);
    }

    @Override
    public ResponseEntity<ResponsePendingValidations> getPendingValidations(Map<String, String> headers, String idClient) {
        PendingValidationsService pendingValidationsService = this.getPendingValidationsService();
        Call<ResponsePendingValidations> p = pendingValidationsService.getPendingValidations(headers, idClient);

        try {
            Response<ResponsePendingValidations> response = p.execute();
            return getResponseEntity(response);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
        }
    }

    private ResponseEntity<ResponsePendingValidations> getResponseEntity(Response<ResponsePendingValidations> response) throws IOException {
        if (response.code() == HttpStatus.BAD_REQUEST.value() ){
            String errorMessage =response.errorBody().string() ;
            throw new ServiceException(response.code(), errorMessage);
        }
        return new ResponseEntity<>(response.body(), HttpStatus.valueOf(response.code()));
    }
}


interface PendingValidationsService{

    @GET("clients/pendingvalidations/{idClient}")
    Call<ResponsePendingValidations> getPendingValidations(
            @HeaderMap Map<String, String> headers,
            @Path("idClient") String idClient
    );
}
