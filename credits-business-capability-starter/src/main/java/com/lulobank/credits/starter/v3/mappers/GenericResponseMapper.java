package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenericResponseMapper {

    GenericResponseMapper INSTANCE = Mappers.getMapper(GenericResponseMapper.class);

    @Mapping(source = "businessCode", target = "code")
    @Mapping(source = "providerCode", target = "failure")
    ErrorResponse toErrorResponse(UseCaseResponseError savingsAccountResponseError);
}
