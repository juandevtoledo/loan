package com.lulobank.credits.starter.v3.adapters.out.otp;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpRequest;
import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import com.lulobank.otp.sdk.dto.credits.ValidateOtpForNewLoan;
import com.lulobank.otp.sdk.operations.OtpCreditOperations;

import io.vavr.control.Either;

public class ValidateOtpServiceAdapterTest {

	private ValidateOtpServiceAdapter subject;

	@Mock
	private OtpCreditOperations otpCreditOperations;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new ValidateOtpServiceAdapter(otpCreditOperations);
	}
	
	@Test
	public void validateOtpShouldReturnTrue() {
		ValidateOtpRequest validateOtpRequest = buildValidateOtpRequest();
		when(otpCreditOperations.verifyHireCreditOperation(eq(validateOtpRequest.getAuth()),
				isA(ValidateOtpForNewLoan.class), eq(validateOtpRequest.getIdClient()))).thenReturn(true);
		Either<UseCaseResponseError, ValidateOtpResponse> response = subject.validateOtp(validateOtpRequest);
		assertTrue(response.isRight());
		assertThat(response.get().isValid(), is(true));
	}
	
	@Test
	public void validateOtpShouldReturnFalse() {
		ValidateOtpRequest validateOtpRequest = buildValidateOtpRequest();
		when(otpCreditOperations.verifyHireCreditOperation(eq(validateOtpRequest.getAuth()),
				isA(ValidateOtpForNewLoan.class), eq(validateOtpRequest.getIdClient()))).thenReturn(false);
		Either<UseCaseResponseError, ValidateOtpResponse> response = subject.validateOtp(validateOtpRequest);
		assertTrue(response.isRight());
		assertThat(response.get().isValid(), is(false));
	}
	
	@Test
	public void validateOtpShouldReturnLeft() {
		ValidateOtpRequest validateOtpRequest = buildValidateOtpRequest();
		when(otpCreditOperations.verifyHireCreditOperation(eq(validateOtpRequest.getAuth()),
				isA(ValidateOtpForNewLoan.class), eq(validateOtpRequest.getIdClient()))).thenThrow(new RuntimeException());
		Either<UseCaseResponseError, ValidateOtpResponse> response = subject.validateOtp(validateOtpRequest);
		assertTrue(response.isLeft());
	}

	private ValidateOtpRequest buildValidateOtpRequest() {
		return ValidateOtpRequest.builder()
				.idClient("idClient")
				.idCredit("idCredit")
				.idOffer("idOffer")
				.otp("otp")
				.auth(new HashMap<String, String>())
				.build();
	}
 }
