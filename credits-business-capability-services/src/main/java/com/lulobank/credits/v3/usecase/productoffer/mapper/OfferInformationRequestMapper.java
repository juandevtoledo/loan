package com.lulobank.credits.v3.usecase.productoffer.mapper;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.usecase.productoffer.command.GenerateOfferRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OfferInformationRequestMapper {

    OfferInformationRequestMapper INSTANCE = Mappers.getMapper(OfferInformationRequestMapper.class);

    @Mapping(target = "idClient", source = "request.idClient")
    @Mapping(target = "interestRate", source = "entity.initialOffer.riskEngineAnalysis.interestRate")
    @Mapping(target = "loanAmount", source = "request.amount")
    @Mapping(target = "clientLoanRequestedAmount", source = "request.amount")
    @Mapping(target = "clientMonthlyAmountCapacity", source = "entity.initialOffer.riskEngineAnalysis.maxAmountInstallment")
    @Mapping(target = "feeInsurance", source = "insurance")
    OfferInformationRequest toOfferInformationRequest(GenerateOfferRequest request, CreditsV3Entity entity, Double insurance);
}
