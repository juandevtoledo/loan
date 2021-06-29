package com.lulobank.credits.sdk.operations.util;

import com.lulobank.utils.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import retrofit2.Response;

import java.io.IOException;

public class RetrofitsUtil<T> {

    private Integer statusCode;

    public RetrofitsUtil() {
        this.statusCode = HttpStatus.OK.value();
    }

    public  RetrofitsUtil(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public  ResponseEntity<T> getResponseEntity(Response<T> response) throws IOException {

        if (response.code() != statusCode ){
            try{
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                throw new ServiceException(response.code(), errorBody);
            } catch (IOException e) {
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            }
        }
        return new ResponseEntity<>(response.body(), HttpStatus.valueOf(response.code()));
    }
}
