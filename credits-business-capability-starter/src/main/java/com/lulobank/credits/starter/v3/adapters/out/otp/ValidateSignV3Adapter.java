package com.lulobank.credits.starter.v3.adapters.out.otp;

import com.lulobank.credits.starter.v3.mappers.ValidateSingMapper;
import com.lulobank.credits.v3.port.in.promissorynote.ValidForPromissoryNoteSing;
import com.lulobank.credits.v3.port.in.promissorynote.dto.SignPromissoryNoteResponse;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.otp.sdk.operations.OtpCreditOperations;
import io.vavr.control.Try;
import lombok.CustomLog;

import java.util.Map;

@CustomLog
public class ValidateSignV3Adapter implements ValidForPromissoryNoteSing {


    private final OtpCreditOperations otpCreditOperations;

    public ValidateSignV3Adapter(OtpCreditOperations otpCreditOperations) {
        this.otpCreditOperations = otpCreditOperations;
    }

    @Override
    public Try<SignPromissoryNoteResponse> execute(AcceptOffer acceptOffer, Map<String,String> auth) {
        return Try.of(()-> otpCreditOperations.verifyHireCreditOperation(auth,
                ValidateSingMapper.INSTANCE.toValidateOtpForNewLoan(acceptOffer),
                acceptOffer.getIdClient()))
                .map(SignPromissoryNoteResponse::new)
                .onFailure(exception -> log.error("Error connecting with otp service", exception));
    }
}
