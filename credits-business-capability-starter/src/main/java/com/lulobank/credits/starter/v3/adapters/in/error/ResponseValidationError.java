package com.lulobank.credits.starter.v3.adapters.in.error;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static com.lulobank.credits.v3.util.HttpDomainStatus.BAD_REQUEST;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_104;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.VALIDATION_DETAIL;

public class ResponseValidationError {

    public static ResponseEntity<AdapterResponse> of(BindingResult bindingResult){
        return bindingResult.getFieldErrors().stream()
                .findFirst()
                .map(fieldError ->  new ErrorResponse(String.valueOf(BAD_REQUEST.value()), CRE_104.name(), VALIDATION_DETAIL))
                .map(errorResponse -> AdapterResponseUtil.error(errorResponse,HttpStatus.BAD_REQUEST))
                .orElseGet(()-> ResponseEntity.badRequest().build());
    }
}
