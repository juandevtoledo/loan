package com.lulobank.credits.starter.outboundadapter.mapper;

import com.lulobank.credits.sdk.dto.acceptofferv2.CreditWithOffer;
import com.lulobank.otp.sdk.dto.credits.ValidateOtpForNewLoan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VerifyPublicCreditOperationMapper {

    VerifyPublicCreditOperationMapper INSTANCE = Mappers.getMapper( VerifyPublicCreditOperationMapper.class );

    @Mappings({
            @Mapping(source = "idCredit", target = "idCredit"),
            @Mapping(source = "selectedCredit.idOffer", target = "idOffer"),
            @Mapping(source = "confirmationLoanOTP", target = "otp")
    })
    ValidateOtpForNewLoan creditWithOfferToVerifyPublicCreditOperation(CreditWithOffer creditWithOffer);

}
