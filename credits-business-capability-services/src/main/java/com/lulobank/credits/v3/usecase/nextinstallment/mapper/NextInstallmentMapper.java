package com.lulobank.credits.v3.usecase.nextinstallment.mapper;

import com.lulobank.credits.sdk.dto.clientloandetail.LoanStateHomologate;
import com.lulobank.credits.v3.port.in.nextinstallment.dto.Flag;
import com.lulobank.credits.v3.port.in.nextinstallment.dto.NextInstallment;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.usecase.nextinstallment.InstallmentState;
import io.vavr.control.Option;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Mapper(imports = {Option.class, Flag.class})
public interface NextInstallmentMapper {

    NextInstallmentMapper INSTANCE = Mappers.getMapper(NextInstallmentMapper.class);
    String getFlagsByState = "java(Option.of(installmentState).map(state->state.flagsByState(loanInformation, " +
            "creditsEntity)).getOrElse(Flag.empty()))";

    @Mapping(target = "installmentState", source = "installmentState")
    @Mapping(target = "flags", expression = getFlagsByState)
    @Mapping(target = "idCredit", source = "creditsEntity", qualifiedByName = "idCredit")
    @Mapping(target = "idLoanCBS", source = "loanInformation.loanId")
    @Mapping(target = "loanPurpose", source = "creditsEntity.loanRequested.purpose")
    @Mapping(target = "requestedAmount.amount.value", source = "loanInformation.loanAmount.value")
    @Mapping(target = "requestedAmount.currency.value", source = "loanInformation.loanAmount.currency")
    @Mapping(target = "payOffAmount.amount.value", source = "loanInformation.totalBalance.value")
    @Mapping(target = "payOffAmount.currency.value", source = "loanInformation.totalBalance.currency")
    @Mapping(target = "nextInstallmentAmount.amount.value", source = "loanInformation.installmentExpected.value")
    @Mapping(target = "nextInstallmentAmount.currency.value", source = "loanInformation.installmentExpected.currency")
    @Mapping(target = "state", source = "loanInformation", qualifiedByName = "state")
    NextInstallment nextInstallmentTO(CreditsV3Entity creditsEntity, LoanInformation loanInformation, InstallmentState installmentState);

    @Named("idCredit")
    default String getIdCredit(CreditsV3Entity creditsEntity) {
        return Option.of(creditsEntity.getIdCredit())
                .map(UUID::toString)
                .getOrElse(StringUtils.EMPTY);
    }

    @Named("state")
    default String getState(LoanInformation loanInformation) {
        return Option.of(loanInformation)
                .map(LoanInformation::getState)
                .map(LoanStateHomologate::getStatusHomologate)
                .getOrElse(EMPTY);
    }


}
