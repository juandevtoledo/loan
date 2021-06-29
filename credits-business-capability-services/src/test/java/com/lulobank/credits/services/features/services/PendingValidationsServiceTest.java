package com.lulobank.credits.services.features.services;

import com.lulobank.credits.sdk.dto.ResponsePendingValidations;
import com.lulobank.credits.sdk.operations.IPendingValidationsOperations;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class PendingValidationsServiceTest {

    @Mock
    IPendingValidationsOperations pendingValidationsOperations;

    private PendingValidationsService testedClass;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        this.testedClass = new PendingValidationsService(pendingValidationsOperations);
    }

    @Test
    public void Validate_CreateClient_Method(){
        ResponseEntity<ResponsePendingValidations> responsePendingValidationsResponseEntity = new ResponseEntity<>(new ResponsePendingValidations(), HttpStatus.CREATED);
        when(pendingValidationsOperations.getPendingValidations(anyMapOf(String.class, String.class), anyString())).thenReturn(responsePendingValidationsResponseEntity);
        testedClass.getPendingValidations("MNW1234");
        assertEquals(HttpStatus.CREATED, responsePendingValidationsResponseEntity.getStatusCode());
    }
}
