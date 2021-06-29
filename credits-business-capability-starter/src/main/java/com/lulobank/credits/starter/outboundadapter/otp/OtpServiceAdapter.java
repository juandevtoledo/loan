package com.lulobank.credits.starter.outboundadapter.otp;

import com.lulobank.credits.sdk.dto.acceptofferv2.CreditWithOffer;
import com.lulobank.credits.services.port.inbound.OtpService;
import com.lulobank.credits.starter.outboundadapter.mapper.VerifyPublicCreditOperationMapper;
import com.lulobank.otp.sdk.dto.credits.ValidateOtpForNewLoan;
import com.lulobank.otp.sdk.operations.OtpCreditOperations;
import io.vavr.control.Try;

import java.util.Objects;

public class OtpServiceAdapter implements OtpService {


    private final OtpCreditOperations otpCreditOperations;

    public OtpServiceAdapter(OtpCreditOperations otpCreditOperations) {
        this.otpCreditOperations = otpCreditOperations;
    }


    @Override
    public Try<Boolean> validateOffer(CreditWithOffer creditWithOffer) {

        ValidateOtpForNewLoan operation = VerifyPublicCreditOperationMapper.INSTANCE.creditWithOfferToVerifyPublicCreditOperation(creditWithOffer);

        return Try.of(() -> otpCreditOperations.verifyHireCreditOperation(creditWithOffer.getAuthorizationHeader(), operation, creditWithOffer.getIdClient()))
                .filter(Objects::nonNull)
                .map(r -> r);
    }

}
