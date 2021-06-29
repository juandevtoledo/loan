package com.lulobank.credits.v3.port.out.otp;

import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpRequest;
import com.lulobank.credits.v3.port.out.otp.dto.ValidateOtpResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;

public interface ValidateOtpService {

	Either<UseCaseResponseError, ValidateOtpResponse> validateOtp(ValidateOtpRequest validateOtpRequest);
}
