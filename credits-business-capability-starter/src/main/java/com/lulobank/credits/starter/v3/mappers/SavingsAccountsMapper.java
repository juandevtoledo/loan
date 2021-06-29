package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.savingsaccounts.sdk.dto.createsavingaccountv3.CreateSavingsAccountRequest;
import com.lulobank.savingsaccounts.sdk.dto.createsavingaccountv3.SavingAccountCreated;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountsMapper {

    SavingsAccountsMapper INSTANCE = Mappers.getMapper(SavingsAccountsMapper.class);

    @Mapping(target = "simpleDeposit", constant = "false")
    CreateSavingsAccountRequest toSavingsAccountRequest(SavingsAccountRequest savingsAccountRequest);

    SavingsAccountResponse toSavingsAccountResponse(SavingAccountCreated savingAccountCreated);

}
