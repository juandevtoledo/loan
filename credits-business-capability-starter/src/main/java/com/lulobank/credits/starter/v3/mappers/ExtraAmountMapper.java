package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.ExtraAmountResponse;
import com.lulobank.credits.v3.usecase.installment.dto.ExtraAmountInstallmentResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExtraAmountMapper {
    ExtraAmountMapper INSTANCE = Mappers.getMapper(ExtraAmountMapper.class);

    ExtraAmountResponse toExtraAmountResponse(ExtraAmountInstallmentResult extraAmountInstallmentResult);

}
