package com.lulobank.credits.services.features.services;

import com.lulobank.credits.sdk.dto.ResponsePendingValidations;
import com.lulobank.credits.sdk.operations.IPendingValidationsOperations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class PendingValidationsService {

    private IPendingValidationsOperations pendingValidationsOperations;

    private Map<String, String> headers;

    public PendingValidationsService(IPendingValidationsOperations pendingValidationsOperations){
        this.pendingValidationsOperations = pendingValidationsOperations;
        headers = new HashMap<>();
    }

    public ResponsePendingValidations getPendingValidations(String idClient){
        ResponseEntity<ResponsePendingValidations> response = this.pendingValidationsOperations.getPendingValidations(headers, idClient);
        return response.getBody();
    }
}
