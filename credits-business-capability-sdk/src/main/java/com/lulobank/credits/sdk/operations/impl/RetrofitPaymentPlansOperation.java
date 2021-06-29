package com.lulobank.credits.sdk.operations.impl;

import java.util.Map;
import java.util.function.Function;

import org.springframework.http.HttpStatus;

import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanRequestV3;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanResponseV3;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanResponseV4;
import com.lulobank.credits.sdk.operations.PaymentPlansOperation;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.CustomLog;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

@CustomLog
public class RetrofitPaymentPlansOperation implements PaymentPlansOperation {

    private PaymentPlansOperationService service;

    public RetrofitPaymentPlansOperation(Retrofit retrofit) {
        this.service = retrofit.create(PaymentPlansOperationService.class);
    }

    @Override
    public Either<ResponseBody, PaymentPlanResponseV3> getPaymentPlansByClient(Map<String, String> headers, PaymentPlanRequestV3 request) {
        Call<PaymentPlanResponseV3> call = this.service.getPaymentPlans(headers, request, request.getIdClient());
        return Try.of(call::execute)
                .map(getResponse())
                .recover(getErrorResponse(request)).get();
    }

    @Override
    public Either<ResponseBody, PaymentPlanResponseV4> getPaymentPlansV4ByClient(Map<String, String> headers, PaymentPlanRequestV3 request) {
        Call<PaymentPlanResponseV4> call = this.service.getPaymentPlansV4(headers, request, request.getIdClient());
        return Try.of(call::execute)
                .map(getResponseV4())
                .recover(getErrorResponseV4(request)).get();
    }

    public Function<Throwable, Either<ResponseBody, PaymentPlanResponseV4>> getErrorResponseV4(PaymentPlanRequestV3 request) {
        return error -> {
            log.error(String.format("Error Getting PaymentPlantList By Client , idClient : %s, msg : %s ", request.getIdClient(), error.getMessage()), error);
            return Either.left(ResponseBody.create(MediaType.get("application/json"), "{ \"error\": \"Error Trying Connect to  Credit Services\"}"));
        };
    }

    @Deprecated
    public Function<Throwable, Either<ResponseBody, PaymentPlanResponseV3>> getErrorResponse(PaymentPlanRequestV3 request) {
        return error -> {
            log.error(String.format("Error Getting PaymentPlantList By Client , idClient : %s, msg : %s ", request.getIdClient(), error.getMessage()), error);
            return Either.left(ResponseBody.create(MediaType.get("application/json"), "{ \"error\": \"Error Trying Connect to  Credit Services\"}"));
        };
    }

    private Function<Response<PaymentPlanResponseV4>, Either<ResponseBody, PaymentPlanResponseV4>> getResponseV4() {
        return response -> {
            if (HttpStatus.OK.value() == response.code()) {
                return Either.right(response.body());
            } else {
                return Either.left(response.errorBody());
            }
        };
    }

    @Deprecated
    private Function<Response<PaymentPlanResponseV3>, Either<ResponseBody, PaymentPlanResponseV3>> getResponse() {
        return response -> {
            if (HttpStatus.OK.value() == response.code()) {
                return Either.right(response.body());
            } else {
                return Either.left(response.errorBody());
            }
        };
    }

    private interface PaymentPlansOperationService {
        @POST("credits/v3/client/{idClient}/payment-plan")
        Call<PaymentPlanResponseV3> getPaymentPlans(@HeaderMap Map<String, String> headers,
                                                    @Body PaymentPlanRequestV3 request,
                                                    @Path("idClient") String idClient
        );

        @POST("credits/v4/client/{idClient}/payment-plan")
        Call<PaymentPlanResponseV4> getPaymentPlansV4(@HeaderMap Map<String, String> headers,
                                                      @Body PaymentPlanRequestV3 request,
                                                      @Path("idClient") String idClient
        );
    }
}
