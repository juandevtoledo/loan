package com.lulobank.credits.v3.usecase.preappoveloanoffers.mapper;

import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.service.OffersTypeV3;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.dto.OfferedResponse;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.dto.SimulationInstallment;
import com.lulobank.credits.v3.util.RoundNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Mapper(imports = {OffersTypeV3.class,BigDecimal.class, RoundNumber.class})
public interface OfferResponseMapper {
    OfferResponseMapper INSTANCE = Mappers.getMapper(OfferResponseMapper.class);
    String CREDITSV3_ENTITY = "creditsV3Entity";


    @Mapping(target = "amount", source = "initialOffer.amount")
    @Mapping(target = "maxAmountInstallment", source = "initialOffer.maxAmountInstallment")
    @Mapping(target = "maxTotalAmount", source = "initialOffer.maxAmount")
    @Mapping(target = "offer.type", expression = "java(OffersTypeV3.FLEXIBLE_LOAN.name())")
    @Mapping(target = "offer.name", expression = "java(OffersTypeV3.FLEXIBLE_LOAN.getDescription())")
    @Mapping(target = "offer.insuranceCost", source = CREDITSV3_ENTITY, qualifiedByName = "insuranceCost")
    @Mapping(target = "offer.amount", source = CREDITSV3_ENTITY, qualifiedByName = "amount")
    @Mapping(target = "offer.idOffer", source = CREDITSV3_ENTITY, qualifiedByName = "getIdOffer")
    @Mapping(target = "offer.simulateInstallment", source =CREDITSV3_ENTITY, qualifiedByName = "simulateInstallmentList")
    @Mapping(target = "idCredit", expression = "java(creditsV3Entity.getIdCredit().toString())")
    OfferedResponse offeredResponseTo(CreditsV3Entity creditsV3Entity);

    @Mapping(target = "monthlyNominalRate", expression = "java(RoundNumber.defaultScale(flexibleLoanV3.getMonthlyNominalRate()))")
    @Mapping(target = "interestRate", expression = "java(RoundNumber.defaultScale(flexibleLoanV3.getInterestRate()))")
    SimulationInstallment simulateInstallmentTo(FlexibleLoanV3 flexibleLoanV3);

    @Named("simulateInstallmentList")
    default List<SimulationInstallment> convertList(CreditsV3Entity source) {

        return source.getInitialOffer()
                .getOfferEntities()
                .stream()
                .findFirst()
                .map(OfferEntityV3::getFlexibleLoans)
                .map(flexibleLoanV3s -> flexibleLoanV3s.stream().map(OfferResponseMapper.INSTANCE::simulateInstallmentTo))
                .map(stream -> stream.collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    @Named("idOffer")
    default String getIdOffer(CreditsV3Entity source) {
        return source.getInitialOffer().getOfferEntities()
                .stream()
                .findFirst()
                .map(OfferEntityV3::getIdOffer)
                .orElse(EMPTY);
    }
    @Named("insuranceCost")
    default Double getInsuranceCost(CreditsV3Entity source) {
        return source.getInitialOffer().getOfferEntities()
                .stream()
                .findFirst()
                .map(OfferEntityV3::getFeeInsurance)
                .orElse(Double.NaN);
    }

    @Named("amount")
    default BigDecimal amount(CreditsV3Entity source) {
        return BigDecimal.valueOf(source.getInitialOffer().getAmount());
    }


}
