package com.lulobank.credits.sdk.operations.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lulobank.credits.sdk.dto.CreditSuccessResult;
import com.lulobank.credits.sdk.dto.clientloandetail.Loan;
import com.lulobank.credits.sdk.dto.loandetails.LoanDetailRetrofitResponse;
import com.lulobank.credits.sdk.operations.IGetLoanDetailOperations;
import com.lulobank.utils.exception.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RetrofitGetLoanDetailOperations implements IGetLoanDetailOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrofitGetLoanDetailOperations.class);

    private final Retrofit retrofit;

    public RetrofitGetLoanDetailOperations(String url) {
        super();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }

    protected Retrofit getRetrofit() {
        return this.retrofit;
    }

    private RetrofitGetLoanDetailServices getRetrofitGetLoanDetailServices() {
        return this.getRetrofit().create(RetrofitGetLoanDetailServices.class);
    }

    @Override
    public LoanDetailRetrofitResponse getCreditsProducts(Map<String, String> headers, String idClient) {
        
        RetrofitGetLoanDetailServices service = this.getRetrofitGetLoanDetailServices();
        Call<LoanDetailRetrofitResponse> p = service.getCreditsProducts(headers,idClient);
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            Response<LoanDetailRetrofitResponse> response = p.execute();
            if (response.body() != null) {
                String jsonStr = mapper.writeValueAsString(response.body());
                LOGGER.info("getCreditsProducts GET: {}", jsonStr);
            }
            return getLoanDetailsEntityByRetrofitResponse(response);
        } catch (ServiceException  | IOException e) {
            LOGGER.error("Error Get Credits Products : {}",e.getMessage() ,e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e);
        }
    }

    @Override
    public List<Loan> getClientLoan(Map<String, String> headers, String idClient){
        RetrofitGetLoanDetailServices service = this.getRetrofitGetLoanDetailServices();
        Call<CreditSuccessResult<List<Loan>>> p = service.getClientLoan(headers,idClient);

        try {
            Response<CreditSuccessResult<List<Loan>>> response = p.execute();
            return getResponseLoanClientByRetrofitResponse(response);
        } catch (ServiceException  | IOException e) {
            LOGGER.error("Error getting customer loan information : {}",e.getMessage() ,e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @NotNull
    private LoanDetailRetrofitResponse getLoanDetailsEntityByRetrofitResponse(Response<LoanDetailRetrofitResponse> response) {

        if (response.code() != HttpStatus.OK.value()) {
            try {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                throw new ServiceException(response.code(), errorBody);
            } catch (IOException e) {
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            }
        }
        return response.body();
    }
    
    @NotNull
    private List<Loan> getResponseLoanClientByRetrofitResponse(Response<CreditSuccessResult<List<Loan>>> response) throws IOException {
        if (response.code() != HttpStatus.OK.value()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            throw new ServiceException(response.code(), errorBody);
        }
        return response.body().getContent();
    }

    private interface RetrofitGetLoanDetailServices {

        @GET("credits/{idClient}/products")
        Call<LoanDetailRetrofitResponse> getCreditsProducts(
                @HeaderMap Map<String, String> headers,
                @Path("idClient") String idClient
        );

        @GET("credits/loan/client/{idClient}")
        Call<CreditSuccessResult<List<Loan>>> getClientLoan(
                @HeaderMap Map<String, String> headers,
                @Path("idClient") String idClient
        );
    }
}
