package com.lulobank.credits.v3.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.lulobank.credits.v3.port.in.loan.dto.DisbursementLoanRequest;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;

@Mapper
public interface DisbursementLoanMapper {
	
	DisbursementLoanMapper INSTANCE = Mappers.getMapper(DisbursementLoanMapper.class);
	
	
	@Mapping(target = "idClient", source = "idClient")
	@Mapping(target = "idClientMambu", source = "idClientMambu")
	@Mapping(target = "idCreditMambu", source = "idLoanAccountMambu")
	DisbursementLoanRequest creditsV3EntityToDisbursementLoanRequest(CreditsV3Entity creditsV3Entity);
}
