package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.services.utils.InterestUtil;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePaymentRequest;
import com.lulobank.credits.v3.service.OffersTypeV3;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.usecase.paymentplan.command.GetPaymentPlan;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SimulatePaymentRequestMapper {

    SimulatePaymentRequestMapper INSTANCE = Mappers.getMapper(SimulatePaymentRequestMapper.class);

    @Mapping(target = "installment", source = "offerEntityV3.installments")
    @Mapping(target = "interestRate", source = "offerEntityV3.annualNominalRate")
    SimulatePaymentRequest simulatePaymentRequestFrom(OfferEntityV3 offerEntityV3, GetPaymentPlan getPaymentPlan);

    @Mapping(target = "installment", source = "offersTypeV3.installment")
    @Mapping(target = "amount", source = "offerInformationRequest.loanAmount")
    SimulatePaymentRequest simulatePaymentRequestFrom(OfferInformationRequest offerInformationRequest,OffersTypeV3 offersTypeV3);

    @AfterMapping
    default void setInterestRate(OfferInformationRequest offerInformationRequest, @MappingTarget SimulatePaymentRequest simulatePaymentRequest) {
        if(Objects.nonNull(offerInformationRequest)){
            simulatePaymentRequest.setInterestRate(InterestUtil.getAnnualNominalRate(offerInformationRequest.getInterestRate()));
        }

    }

    @AfterMapping
    default void setInstallment(GetPaymentPlan getPaymentPlan, @MappingTarget SimulatePaymentRequest simulatePaymentRequest) {
        if (Objects.isNull(simulatePaymentRequest.getInstallment())) {
            simulatePaymentRequest.setInstallment(getPaymentPlan.getInstallments());
        }
    }
}


