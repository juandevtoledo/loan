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
public interface LoanAssessmentResultMapper {

    LoanAssessmentResultMapper INSTANCE = Mappers.getMapper(LoanAssessmentResultMapper.class);

    @Mapping(target = "idCredit", expression = "java(UUID.randomUUID())")
    @Mapping(target = "idClient", source = "idClient")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "creditType", expression = "java(CreditType.PREAPPROVED)")
    @Mapping(target = "initialOffer.results", source = "loanAssessmentResultMessage", qualifiedByName = "results")
    CreditsV3Entity toCreditsV3Entity(LoanAssessmentResultMessage loanAssessmentResultMessage);

    @Mapping(target = "schedule", source = "riskResult", qualifiedByName = "schedules")
    @Mapping(target = "maxTotalAmount", source = "loanAmount")
    RiskResult toRiskResultEntity(LoanAssessmentResultMessage.RiskResult riskResult);

    @Mapping(target = "installment", source = "installment")
    @Mapping(target = "interestRate", source = "interestRateEA")
    @Mapping(target = "annualNominalRate", source = "interestRateNA")
    @Mapping(target = "monthlyNominalRate", source = "interestRatePM")
    Schedule toScheduleEntity(LoanAssessmentResultMessage.Schedule schedule);

    @Named("schedules")
    default List<Schedule> getSchedules(LoanAssessmentResultMessage.RiskResult source) {
        return source.getSchedule().stream()
                .map(INSTANCE::toScheduleEntity)
                .collect(Collectors.toList());

    }

    @Named("results")
    default List<RiskResult> getResults(LoanAssessmentResultMessage source) {
        return source.getResults().stream()
                .map(INSTANCE::toRiskResultEntity)
                .collect(Collectors.toList());

    }

}