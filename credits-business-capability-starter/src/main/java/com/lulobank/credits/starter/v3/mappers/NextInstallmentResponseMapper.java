package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.nextinstallment.NextInstallmentResponse;
import com.lulobank.credits.v3.port.in.nextinstallment.dto.NextInstallment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NextInstallmentResponseMapper {

    NextInstallmentResponseMapper INSTANCE = Mappers.getMapper(NextInstallmentResponseMapper.class);

    @Mapping(target = "payOffAmount.value" , source = "payOffAmount.amount.roundValue")
    @Mapping(target = "payOffAmount.currency" , source = "payOffAmount.currency.value")
    @Mapping(target = "nextInstallmentAmount.value" , source = "nextInstallmentAmount.amount.roundValue")
    @Mapping(target = "nextInstallmentAmount.currency" , source = "nextInstallmentAmount.currency.value")
    @Mapping(target = "requestedAmount.value" , source = "requestedAmount.amount.roundValue")
    @Mapping(target = "requestedAmount.currency" , source = "requestedAmount.currency.value")
    @Mapping(target = "installmentDate" , source = "installmentDate",dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    NextInstallmentResponse nextInstallmentResponseTo(NextInstallment nextInstallment);

}
