package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OffersEntityMapper {

    OffersEntityMapper INSTANCE = Mappers.getMapper(OffersEntityMapper.class);

    OfferEntityV3 offerEntityV3From(OfferEntity offerEntity);
}
