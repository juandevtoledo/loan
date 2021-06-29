package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.v3.usecase.intialsoffersv3.command.GetOffersByClient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GetOffersByClientMapper {

    GetOffersByClientMapper INSTANCE = Mappers.getMapper(GetOffersByClientMapper.class);

    GetOffersByClient getOffersByClientTO(GetOfferToClient getOfferToClient);
}
