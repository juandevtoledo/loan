package com.lulobank.credits.v3.port.in.approvedriskengine;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(imports = UUID.class)
public interface RiskEngineResponseMapper {

	RiskEngineResponseMapper INSTANCE = Mappers.getMapper(RiskEngineResponseMapper.class);

	@Mapping(target = "idCredit", expression = "java(UUID.randomUUID())")
	@Mapping(target = "idClient", source = "idClient")
	@Mapping(target = "idProductOffer", source = "idProductOffer")
	@Mapping(target = "creditType", constant = "PREAPPROVED")
	@Mapping(target = "clientInformation.documentId.id", source = "clientInformation.documentId.id")
	@Mapping(target = "clientInformation.documentId.type", source = "clientInformation.documentId.type")
	@Mapping(target = "clientInformation.documentId.issueDate", source = "clientInformation.documentId.issueDate")
	@Mapping(target = "clientInformation.name", source = "clientInformation.name")
	@Mapping(target = "clientInformation.lastName", source = "clientInformation.lastName")
	@Mapping(target = "clientInformation.middleName", source = "clientInformation.middleName")
	@Mapping(target = "clientInformation.secondSurname", source = "clientInformation.secondSurname")
	@Mapping(target = "clientInformation.gender", source = "clientInformation.gender")
	@Mapping(target = "clientInformation.email", source = "clientInformation.email")
	@Mapping(target = "clientInformation.phone.number", source = "clientInformation.phone.number")
	@Mapping(target = "clientInformation.phone.prefix", source = "clientInformation.phone.prefix")
	
	@Mapping(target = "initialOffer.riskEngineAnalysis.amount", source = "riskEngineAnalysis.amount")
	@Mapping(target = "initialOffer.riskEngineAnalysis.interestRate", source = "riskEngineAnalysis.interestRate")
	@Mapping(target = "initialOffer.riskEngineAnalysis.installments", source = "riskEngineAnalysis.installments")
	@Mapping(target = "initialOffer.riskEngineAnalysis.maxAmountInstallment", source = "riskEngineAnalysis.maxAmountInstallment")
	CreditsV3Entity riskEngineResponseMessageToCreditsV3Entity(RiskEngineResponseMessage riskEngineResponseMessage);
}
