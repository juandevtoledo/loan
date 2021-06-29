package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferResponse;
import com.lulobank.credits.v3.port.in.productoffer.dto.ProductOffer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ApprovedProductOfferMapper.class)
public interface ProductOfferResponseMapper {

    ProductOfferResponseMapper INSTANCE = Mappers.getMapper(ProductOfferResponseMapper.class);

    ProductOfferResponse toProductOfferResponse(ProductOffer productOffer);
}
