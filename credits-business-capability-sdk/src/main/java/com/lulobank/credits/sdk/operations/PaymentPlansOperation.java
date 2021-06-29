package com.lulobank.credits.sdk.operations;

import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanRequestV3;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanResponseV3;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanResponseV4;
import io.vavr.control.Either;
import okhttp3.ResponseBody;

import java.util.Map;

public interface PaymentPlansOperation {

    @Deprecated
    Either<ResponseBody, PaymentPlanResponseV3> getPaymentPlansByClient(Map<String, String> headers, PaymentPlanRequestV3 request);

    Either<ResponseBody, PaymentPlanResponseV4> getPaymentPlansV4ByClient(Map<String, String> headers, PaymentPlanRequestV3 request);
}
