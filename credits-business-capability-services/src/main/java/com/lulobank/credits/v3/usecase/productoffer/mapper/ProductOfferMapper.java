package com.lulobank.credits.v3.usecase.productoffer.mapper;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.in.productoffer.dto.ProductOffer;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import io.vavr.control.Option;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = LocalDateTime.class)
public interface ProductOfferMapper {

    ProductOfferMapper INSTANCE = Mappers.getMapper(ProductOfferMapper.class);

    @Mapping(target = "amount", source = "offer.amount")
    @Mapping(target = "idCredit", expression = "java(getIdCredit(entity))")
    @Mapping(target = "riskModelResponse", source = "entity.initialOffer.typeOffer")
    @Mapping(target = "currentDate", expression = "java(LocalDateTime.now())")
    ProductOffer toOfferInformationRequest(OfferEntityV3 offer, CreditsV3Entity entity);

    @AfterMapping
    default void mapOffers(@MappingTarget ProductOffer productOffer, OfferEntityV3 offer) {
        productOffer.setOffers(Collections.singletonList(OfferMapper.INSTANCE.toOffer(offer)));
    }

    default String getIdCredit(CreditsV3Entity entity) {
        return Option.of(entity).map(ent -> ent.getIdCredit().toString()).getOrElse(EMPTY);
    }
}
