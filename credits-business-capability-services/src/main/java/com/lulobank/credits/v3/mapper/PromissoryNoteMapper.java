package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.v3.dto.DecevalInformationV3;
import com.lulobank.credits.v3.port.in.promissorynote.CreatePromissoryNoteResponseMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromissoryNoteMapper {

    PromissoryNoteMapper INSTANCE = Mappers.getMapper(PromissoryNoteMapper.class);

    @Mapping(target = "decevalCorrelationId", source = "signPassword")
    DecevalInformationV3 toDecevalInformationV3(CreatePromissoryNoteResponseMessage createPromissoryNoteResponseMessage);

}
