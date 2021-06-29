package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.in.loan.dto.LoanResponse;
import flexibility.client.models.request.CreateLoanRequest;
import flexibility.client.models.response.CreateLoanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreateLoanMapper {

    CreateLoanMapper INSTANCE = Mappers.getMapper(CreateLoanMapper.class);

    @Mapping(target = "automaticDebit", constant = "false")
    CreateLoanRequest toCreateLoanRequest(LoanRequest loanRequest);

    LoanResponse toLoanResponse(CreateLoanResponse creditsV3Entity);

}
