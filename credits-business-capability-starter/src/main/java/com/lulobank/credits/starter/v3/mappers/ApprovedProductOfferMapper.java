package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.ApprovedProductOffer;
import com.lulobank.credits.v3.port.in.productoffer.dto.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {OfferInstallmentMapper.class})
public interface ApprovedProductOfferMapper {

    ApprovedProductOfferMapper INSTANCE = Mappers.getMapper(ApprovedProductOfferMapper.class);

    ApprovedProductOffer toApprovedProductOffer(Offer offer);
}
