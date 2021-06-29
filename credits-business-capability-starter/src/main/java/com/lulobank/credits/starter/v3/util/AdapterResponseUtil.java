package com.lulobank.credits.starter.v3.util;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import io.vavr.collection.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterErrorCode.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

public class AdapterResponseUtil {

    private AdapterResponseUtil() {
    }

    public static ResponseEntity<AdapterResponse> ok(AdapterResponse response) {
        return new ResponseEntity<>(response, OK);
    }

    public static ResponseEntity<AdapterResponse> ok() {
        return new ResponseEntity<>(OK);
    }

    public static ResponseEntity<AdapterResponse> success(AdapterResponse response, HttpStatus httpStatus) {
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<AdapterResponse> error(ErrorResponse errorResponse, HttpStatus httpStatus) {
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    public static HttpStatus getHttpStatusFromBusinessCode(String businessCode) {
        return List.of(AdapterErrorCode.values())
                .filter(inboundAdapterErrorCode -> inboundAdapterErrorCode.getBusinessCodes().contains(businessCode))
                .map(AdapterErrorCode::getHttpStatus)
                .getOrElse(INTERNAL_SERVER_ERROR.getHttpStatus());
    }

    public static ResponseEntity accepted() {
        return new ResponseEntity<>(ACCEPTED);
    }

}
