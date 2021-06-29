package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.CreditWithOfferV3Request;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditWithOfferMapper {

    CreditWithOfferMapper INSTANCE = Mappers.getMapper(CreditWithOfferMapper.class);

    AcceptOffer toAcceptOffer(CreditWithOfferV3Request creditWithOfferV3Request,String idClient);

}
