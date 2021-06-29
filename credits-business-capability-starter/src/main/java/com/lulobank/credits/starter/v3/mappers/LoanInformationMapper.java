package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.loan.LoanDetailResponse;
import com.lulobank.credits.v3.usecase.loandetail.dto.LoanDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoanInformationMapper {

    LoanInformationMapper INSTANCE = Mappers.getMapper(LoanInformationMapper.class);


    @Mapping(target = "requestedAmount.value", source = "requestedAmount.amount.roundValue")
    @Mapping(target = "requestedAmount.currency", source = "requestedAmount.currency.value")
    @Mapping(target = "payOffAmount.value", source = "payOffAmount.amount.roundValue")
    @Mapping(target = "payOffAmount.currency", source = "payOffAmount.currency.value")
    @Mapping(target = "paidAmount.value", source = "paidAmount.amount.roundValue")
    @Mapping(target = "paidAmount.currency", source = "paidAmount.currency.value")
    @Mapping(target = "rates.monthlyNominal", source = "rates.monthlyNominal.roundValue")
    @Mapping(target = "rates.annualEffective", source = "rates.annualEffective.roundValue")
    LoanDetailResponse loanDetailResponseTo(LoanDetail loanDetail);
}
