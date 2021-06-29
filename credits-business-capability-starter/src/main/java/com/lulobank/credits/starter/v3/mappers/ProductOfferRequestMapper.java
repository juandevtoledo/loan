package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferRequest;
import com.lulobank.credits.v3.usecase.productoffer.command.GenerateOfferRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductOfferRequestMapper {

    ProductOfferRequestMapper INSTANCE = Mappers.getMapper(ProductOfferRequestMapper.class);

    GenerateOfferRequest toGenerateOfferRequest(ProductOfferRequest request);
}
