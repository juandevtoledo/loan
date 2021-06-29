package com.lulobank.credits.starter.v3.util;

import static org.springframework.http.HttpStatus.valueOf;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.credits.sdk.dto.errorv3.ErrorResultV3;
import com.lulobank.credits.sdk.dto.errorv3.GenericResponse;

import io.vavr.control.Try;
import lombok.CustomLog;

@CustomLog
public class ResponseUtil {

    private ResponseUtil() {
    }

    private static Map<String, Set<String>> map(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField, Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())));
    }

    public static ResponseEntity of(BindingResult bindingResult) {
        return Try.of(() -> {
            final ErrorResultV3 invalidRequest = ErrorResultV3.builder()
                    .detail(new ObjectMapper().writeValueAsString(map(bindingResult)))
                    .failure("INVALID_REQUEST")
                    .build();
            log.error("RequiredValidator -> {}", invalidRequest);
            return buildErrorResponse(invalidRequest, HttpStatus.BAD_REQUEST.value());
        }).onFailure(ex -> log.error("Error in extract error from request", ex))
                .getOrElse(ResponseEntity.badRequest().build());
    }


    public static <T> ResponseEntity<T> buildResponse(T response, HttpStatus status) {
        return new ResponseEntity<>(response, status);
    }

    public static <T> ResponseEntity<T> buildErrorResponse(ErrorResultV3 errorResult, Integer status) {
        return new ResponseEntity(new GenericResponse(errorResult), valueOf(status));
    }
}
