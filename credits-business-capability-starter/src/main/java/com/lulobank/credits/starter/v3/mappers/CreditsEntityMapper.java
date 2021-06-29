package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.starter.v3.adapters.out.dynamo.dto.CreditsDto;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditsEntityMapper {

    CreditsEntityMapper INSTANCE = Mappers.getMapper(CreditsEntityMapper.class);

    CreditsV3Entity toCreditsEntity(CreditsEntity creditsDto);

    CreditsDto toCreditsDto(CreditsV3Entity creditsV3Entity);

    CreditsV3Entity toCredit(CreditsEntity CreditsEntity);

}
