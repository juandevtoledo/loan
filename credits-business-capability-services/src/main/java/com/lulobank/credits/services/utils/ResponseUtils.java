package com.lulobank.credits.services.utils;

import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.credits.sdk.dto.CreditErrorResult;
import com.lulobank.credits.sdk.dto.CreditSuccessResult;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static com.lulobank.core.utils.ValidatorUtils.getHttpStatusByCode;
import static com.lulobank.credits.services.utils.HttpCodes.BAD_REQUEST;
import static com.lulobank.credits.services.utils.HttpCodes.INTERNAL_SERVER_ERROR;

public class ResponseUtils {
    private ResponseUtils() {
    }

    public static final Response getResponseBindingResult(BindingResult bindingResult) {
        String error = bindingResult
                .getAllErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(StringUtils.EMPTY);
        return new Response<>(ValidatorUtils.getListValidations(error,
                BAD_REQUEST));
    }


    @NotNull
    public static final <R>ResponseEntity getResponseEntityError(Response<R> response) {
        return   response.getErrors().stream().findFirst()
                        .map(error -> new ResponseEntity<>(new CreditErrorResult(response.getErrors()),
                                getHttpStatusByCode(error.getValue()))
                        ).orElse(getResponseEntityByStatus(INTERNAL_SERVER_ERROR));
    }

    @NotNull
    public static final ResponseEntity<CreditSuccessResult> getResponseEntityByStatus(Response response, String status) {
        return new ResponseEntity(response.getContent(), getHttpStatusByCode(status));
    }

    public static ResponseEntity getResponseEntityByStatus(String status){
        return ResponseEntity.status(getHttpStatusByCode(status)).build();
    }

}
