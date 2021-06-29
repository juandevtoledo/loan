package com.lulobank.credits.v3.usecase.productoffer.mapper;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.in.productoffer.dto.Offer;
import com.lulobank.credits.v3.util.RoundNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {InstallmentMapper.class}, imports = {RoundNumber.class})
public interface OfferMapper {

    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);

    @Mapping(target = "insuranceCost", source = "offer.feeInsurance")
    @Mapping(target = "simulateInstallment", source = "offer.flexibleLoans")
    @Mapping(target = "monthlyNominalRate", expression = "java(RoundNumber.defaultScale(offer.getMonthlyNominalRate()).floatValue())")
    Offer toOffer(OfferEntityV3 offer);
}
