package com.lulobank.credits.sdk.operations;

import com.lulobank.credits.sdk.dto.ResponsePendingValidations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IPendingValidationsOperations {

    ResponseEntity<ResponsePendingValidations> getPendingValidations(Map<String, String> headers, String idClient);
}

