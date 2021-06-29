package com.lulobank.credits.v3.usecase.intialsoffersv3.mapper;

import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.usecase.intialsoffersv3.command.GetOffersByClient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(imports = {LocalDateTime.class})
public interface InitialOfferV3Mapper {

    InitialOfferV3Mapper INSTANCE = Mappers.getMapper(InitialOfferV3Mapper.class);

    @Mapping(target = "generateDate", expression = "java(LocalDateTime.now())")
    InitialOfferV3 initialOfferV3To(GetOffersByClient getOffersByClient);

}
