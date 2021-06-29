package com.lulobank.credits.starter.v3.adapters.out.otp;

import com.lulobank.credits.v3.port.in.promissorynote.dto.SignPromissoryNoteResponse;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.otp.sdk.operations.OtpCreditOperations;
import com.lulobank.otp.sdk.operations.exceptions.VerifyHireCreditException;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.lulobank.credits.starter.utils.Samples.acceptOfferBuilder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ValidateSignV3AdapterTest {

    @Mock
    private OtpCreditOperations otpCreditOperations;
    private ValidateSignV3Adapter testedClass;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testedClass = new ValidateSignV3Adapter(otpCreditOperations);
    }

    @Test
    public void execute() {
        when(otpCreditOperations.verifyHireCreditOperation(anyMap(), any(), anyString())).thenReturn(true);
        AcceptOffer acceptOffer = acceptOfferBuilder();
        Try<SignPromissoryNoteResponse> reponse = testedClass.execute(acceptOffer, new HashMap<>());
        assertTrue("Not found error", reponse.isSuccess());
        assertTrue("Is Valid", reponse.get().isValid());
    }

    @Test
    public void validationFailed() {
        when(otpCreditOperations.verifyHireCreditOperation(anyMap(), any(), anyString())).thenReturn(false);
        AcceptOffer acceptOffer = acceptOfferBuilder();
        Try<SignPromissoryNoteResponse> reponse = testedClass.execute(acceptOffer, new HashMap<>());
        assertTrue("Not found error", reponse.isSuccess());
        assertFalse("Is not Valid", reponse.get().isValid());
    }

    @Test
    public void verifyHireCreditException() {
        when(otpCreditOperations.verifyHireCreditOperation(anyMap(), any(), anyString())).thenThrow(new VerifyHireCreditException(1, "Error"));
        AcceptOffer acceptOffer = acceptOfferBuilder();
        Try<SignPromissoryNoteResponse> reponse = testedClass.execute(acceptOffer, new HashMap<>());
        assertTrue("Found error", reponse.isFailure());
    }
}
