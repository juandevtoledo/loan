package com.lulobank.credits.v3.port.in.approvedriskengine;

import com.lulobank.credits.v3.dto.CreditType;
import com.lulobank.credits.v3.dto.RiskResult;
import com.lulobank.credits.v3.dto.Schedule;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(imports = {UUID.class, CreditType.class})
public interface RiskEngineResultEventV2Mapper {

    RiskEngineResultEventV2Mapper INSTANCE = Mappers.getMapper(RiskEngineResultEventV2Mapper.class);

    @Mapping(target = "idCredit", expression = "java(UUID.randomUUID())")
    @Mapping(target = "idClient", source = "idClient")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "creditType", expression = "java(CreditType.PREAPPROVED)")
    @Mapping(target = "initialOffer.results", source = "riskEngineResultEventV2Message", qualifiedByName = "results")
    CreditsV3Entity toCreditsV3Entity(RiskEngineResultEventV2Message riskEngineResultEventV2Message);

    @Mapping(target = "schedule", source = "riskResult", qualifiedByName = "schedules")
    @Mapping(target = "maxTotalAmount", source = "loanAmount")
    RiskResult toRiskResultEntity(RiskEngineResultEventV2Message.RiskResult riskResult);

    @Mapping(target = "installment", source = "installment")
    @Mapping(target = "interestRate", source = "interestRateEA")
    @Mapping(target = "annualNominalRate", source = "interestRateNA")
    @Mapping(target = "monthlyNominalRate", source = "interestRatePM")
    Schedule toScheduleEntity(RiskEngineResultEventV2Message.Schedule schedule);

    @Named("schedules")
    default List<Schedule> getSchedules(RiskEngineResultEventV2Message.RiskResult source) {
        return source.getSchedule().stream()
                .map(INSTANCE::toScheduleEntity)
                .collect(Collectors.toList());

    }

    @Named("results")
    default List<RiskResult> getResults(RiskEngineResultEventV2Message source) {
        return source.getResults().stream()
                .map(INSTANCE::toRiskResultEntity)
                .collect(Collectors.toList());

    }

}