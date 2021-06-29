package com.lulobank.credits.v3.usecase.intialsoffersv3.mapper;

import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.usecase.intialsoffersv3.command.GetOffersByClient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OfferInformationRequestMapper {

    OfferInformationRequestMapper INSTANCE = Mappers.getMapper(OfferInformationRequestMapper.class);

    @Mapping(target = "interestRate", source = "getOffersByClient.riskEngineAnalysis.interestRate")
    @Mapping(target = "clientLoanRequestedAmount", source = "getOffersByClient.clientLoanRequestedAmount")
    @Mapping(target = "clientMonthlyAmountCapacity", source = "getOffersByClient.riskEngineAnalysis.maxAmountInstallment")
    @Mapping(target = "loanAmount", source = "getOffersByClient", qualifiedByName = "loanAmount")
    @Mapping(target = "feeInsurance", source = "feeInsurance")
    OfferInformationRequest offerInformationRequestTo(GetOffersByClient getOffersByClient, Double feeInsurance);

    @Named("loanAmount")
    default Double setAmountAndRoundThousands(GetOffersByClient getOffersByClient) {
        Double amountSimulate = Math.min(getOffersByClient.getClientLoanRequestedAmount(), getOffersByClient.getRiskEngineAnalysis().getAmount());
        return Math.floor((amountSimulate / 100000d)) * 100000d;
    }

}
