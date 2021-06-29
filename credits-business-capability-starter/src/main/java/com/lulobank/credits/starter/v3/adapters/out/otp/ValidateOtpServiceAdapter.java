package com.lulobank.credits.starter.v3.adapters.out.otp;

import com.lulobank.credits.v3.port.out.otp.ValidateOtpService;
import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpRequest;
import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpResponse;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import com.lulobank.otp.sdk.dto.credits.ValidateOtpForNewLoan;
import com.lulobank.otp.sdk.operations.OtpCreditOperations;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.CustomLog;

@CustomLog
public class ValidateOtpServiceAdapter implements ValidateOtpService {

	private final OtpCreditOperations otpCreditOperations;
	
	public ValidateOtpServiceAdapter(OtpCreditOperations otpCreditOperations) {
		this.otpCreditOperations = otpCreditOperations;
	}
	
	@Override
	public Either<UseCaseResponseError, ValidateOtpResponse> validateOtp(ValidateOtpRequest validateOtpRequest) {
		return Try
				.of(() -> otpCreditOperations.verifyHireCreditOperation(validateOtpRequest.getAuth(),
						buildValidateOtpForNewLoan(validateOtpRequest), validateOtpRequest.getIdClient()))
				.map(this::buildValidateOtpResponse)
				.onFailure(exception -> log.error("Error connecting with otp service", exception))
				.toEither(CreditsError.validateOtpError());

	}

	private ValidateOtpResponse buildValidateOtpResponse(boolean valid) {
		return ValidateOtpResponse.builder()
				.isValid(valid)
				.build();
	}

	private ValidateOtpForNewLoan buildValidateOtpForNewLoan(ValidateOtpRequest validateOtpRequest) {
		ValidateOtpForNewLoan validateOtpForNewLoan = new ValidateOtpForNewLoan();
		validateOtpForNewLoan.setIdCredit(validateOtpRequest.getIdCredit());
		validateOtpForNewLoan.setIdOffer(validateOtpRequest.getIdOffer());
		validateOtpForNewLoan.setOtp(validateOtpRequest.getOtp());
		return validateOtpForNewLoan;
	}

}
