package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.otp.sdk.dto.credits.ValidateOtpForNewLoan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ValidateSingMapper {

    ValidateSingMapper INSTANCE = Mappers.getMapper(ValidateSingMapper.class);

    @Mapping(source = "confirmationLoanOTP", target = "otp")
    @Mapping(source = "acceptOffer.selectedCredit.idOffer", target = "idOffer")
    ValidateOtpForNewLoan toValidateOtpForNewLoan(AcceptOffer acceptOffer);


}
