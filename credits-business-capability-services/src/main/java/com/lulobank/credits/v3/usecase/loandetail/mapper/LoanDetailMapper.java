package com.lulobank.credits.v3.usecase.loandetail.mapper;

import com.lulobank.credits.sdk.dto.clientloandetail.LoanStateHomologate;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.usecase.loandetail.dto.LoanDetail;
import com.lulobank.credits.v3.vo.loan.CurrencyVO;
import io.vavr.control.Option;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityLoanMovementsPaidStatesEnum.GRACE;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityLoanMovementsPaidStatesEnum.PAID;
import static org.apache.logging.log4j.util.Strings.EMPTY;

@Mapper(imports = {CurrencyVO.class, LoanStateHomologate.class})
public interface LoanDetailMapper {

    LoanDetailMapper INSTANCE = Mappers.getMapper(LoanDetailMapper.class);

    @Mapping(target = "createOn", source = "loan.creationOn")
    @Mapping(target = "closedDate", source = "creditsEntity.closedDate")
    @Mapping(target = "idCredit", source = "creditsEntity", qualifiedByName = "idCredit")
    @Mapping(target = "idLoanCBS", source = "loan.loanId")
    @Mapping(target = "state", source = "loan", qualifiedByName = "state")
    @Mapping(target = "payOffAmount.amount.value", source = "loan.totalBalance.value")
    @Mapping(target = "payOffAmount.currency.value", source = "loan.totalBalance.currency")
    @Mapping(target = "requestedAmount.amount.value", source = "loan.loanAmount.value")
    @Mapping(target = "requestedAmount.currency.value", source = "loan.loanAmount.currency")
    @Mapping(target = "rates.monthlyNominal.value", source = "loan.rates.monthlyNominal")
    @Mapping(target = "rates.annualEffective.value", source = "loan.rates.annualEffective")
    @Mapping(target = "paidAmount.amount.value", source = "paidAmount")
    @Mapping(target = "paidAmount.currency", expression = "java(CurrencyVO.defaultCurrency())")
    @Mapping(target = "installments", source = "loan", qualifiedByName = "countInstallments")
    @Mapping(target = "paidInstallments", source = "loan", qualifiedByName = "countPaidInstallments")
    @Mapping(target = "paymentPlanList", source = "loan.paymentPlanList")
    LoanDetail loanDetailTo(CreditsV3Entity creditsEntity, LoanInformation loan, BigDecimal paidAmount);

    @Named("idCredit")
    default String getIdCredit(CreditsV3Entity creditsEntity) {
        return Option.of(creditsEntity.getIdCredit())
                .map(UUID::toString)
                .getOrElse(StringUtils.EMPTY);
    }

    @Named("countPaidInstallments")
    default Integer getPaidInstallments(LoanInformation loan) {
        return Math.toIntExact(loan.getPaymentPlanList()
                .stream()
                .filter(installment -> PAID.name().equals(installment.getState()))
                .count());
    }

    @Named("countInstallments")
    default Integer getInstallments(LoanInformation loan) {
        return Math.toIntExact(loan.getPaymentPlanList()
                .stream()
                .filter(installment -> !GRACE.name().equals(installment.getState()))
                .count());
    }

    @Named("state")
    default String getState(LoanInformation loanInformation) {
        return Option.of(loanInformation)
                .map(LoanInformation::getState)
                .map(LoanStateHomologate::getStatusHomologate)
                .getOrElse(EMPTY);
    }
}
