package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteRequest;
import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteResponse;
import com.lulobank.promissorynote.sdk.dto.CreatePromissoryNoteClientAndSign;
import com.lulobank.promissorynote.sdk.dto.CreatePromissoryNoteClientAndSignResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromissoryNoteMapper {

    PromissoryNoteMapper INSTANCE = Mappers.getMapper(PromissoryNoteMapper.class);

    CreatePromissoryNoteClientAndSign toCreatePromissoryNoteClientAndSign(PromissoryNoteRequest promissoryNoteRequest);

    PromissoryNoteResponse toCreatePromissoryNoteClientAndSignResponse(CreatePromissoryNoteClientAndSignResponse createPromissoryNoteClientAndSignResponse);

}
