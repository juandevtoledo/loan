package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.services.utils.InterestUtil;
import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import com.lulobank.credits.v3.service.OffersTypeV3;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Mapper(imports = {UUID.class})
public interface OfferEntityV3Mapper {

    OfferEntityV3Mapper INSTANCE = Mappers.getMapper(OfferEntityV3Mapper.class);

    @Mapping(target = "idOffer", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "amount", source = "offerInformationRequest.loanAmount")
    @Mapping(target = "installments", source = "offersTypeV3.installment")
    @Mapping(target = "amountInstallment", source = "simulatePayment.totalDue")
    @Mapping(target = "interestRate", source = "offerInformationRequest.interestRate")
    @Mapping(target = "interestAmount", source = "simulatePayment.interestDue")
    @Mapping(target = "insurance", source = "simulatePayment.feesDue")
    @Mapping(target = "type", source = "offersTypeV3")
    @Mapping(target = "name", source = "offersTypeV3.description")
    @Mapping(target = "feeInsurance", source = "offerInformationRequest.feeInsurance")
    OfferEntityV3 offerEntityV3To(SimulatePayment simulatePayment, OfferInformationRequest offerInformationRequest, OffersTypeV3 offersTypeV3);

    @Mapping(target = "idOffer", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "amount", source = "offerInformationRequest.loanAmount")
    @Mapping(target = "interestRate", source = "offerInformationRequest.interestRate")
    @Mapping(target = "type", source = "offersTypeV3")
    @Mapping(target = "name", source = "offersTypeV3.description")
    @Mapping(target = "feeInsurance", source = "offerInformationRequest.feeInsurance")
    OfferEntityV3 offerEntityV3To(OfferInformationRequest offerInformationRequest, List<FlexibleLoanV3> flexibleLoanV3s, OffersTypeV3 offersTypeV3);

    @Mapping(target = "idOffer", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "amount", source = "clientLoanRequestedAmount")
    @Mapping(target = "type", source = "offersTypeV3")
    @Mapping(target = "name", source = "offersTypeV3.description")
    OfferEntityV3 offerEntityV3To(BigDecimal clientLoanRequestedAmount, List<FlexibleLoanV3> flexibleLoanV3s, OffersTypeV3 offersTypeV3, Double feeInsurance);


    @AfterMapping
    default void setInterestRate(@MappingTarget OfferEntityV3 offerEntityV3To) {
        offerEntityV3To.setAnnualNominalRate(InterestUtil.getAnnualNominalRate(offerEntityV3To.getInterestRate()));
        offerEntityV3To.setMonthlyNominalRate(InterestUtil.getMonthlyNominalRate(offerEntityV3To.getInterestRate()));
    }

    @AfterMapping
    default void setFlexibleLonas(@MappingTarget OfferEntityV3 offerEntityV3To, List<FlexibleLoanV3> flexibleLoanV3s) {
        offerEntityV3To.setFlexibleLoans(flexibleLoanV3s);
    }
}
