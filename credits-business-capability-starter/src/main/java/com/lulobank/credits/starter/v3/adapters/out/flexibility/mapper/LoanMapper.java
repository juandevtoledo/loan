package com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper;

import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentPlan;
import com.lulobank.credits.v3.port.out.corebanking.dto.Rates;
import flexibility.client.models.response.GetLoanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.lulobank.credits.services.utils.InterestUtil.getAnnualEffectiveRateFromAnnualNominalRate;
import static com.lulobank.credits.services.utils.InterestUtil.getMonthlyNominalRateFromAnnualNominalRate;

@Mapper(imports = {LocalDateTime.class, LocalTime.class})
public interface LoanMapper {

    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);

    @Mapping(target = "installmentExpectedDue.value", source = "installmentExpectedDue.amount")
    @Mapping(target = "installmentExpectedDue.currency", source = "installmentExpectedDue.currency")
    @Mapping(target = "totalBalance.value", source = "totalBalance.amount")
    @Mapping(target = "totalBalance.currency", source = "totalBalance.currency")
    @Mapping(target = "loanAmount.value", source = "loanAmount.amount")
    @Mapping(target = "loanAmount.currency", source = "loanAmount.currency")
    @Mapping(target = "rates", source = "getLoanResponse", qualifiedByName = "rates")
    @Mapping(target = "installmentAccruedDue.value", source = "installmentAccruedDue.amount")
    @Mapping(target = "installmentAccruedDue.currency", source = "installmentAccruedDue.currency")
    @Mapping(target = "loanId", source = "id")
    @Mapping(target = "creationOn", source = "creationDate")
    @Mapping(target = "paymentPlanList", source = "getLoanResponse", qualifiedByName = "payments")
    @Mapping(target = "state", source = "accountState")
    @Mapping(target = "cutOffDate", expression = "java(LocalDateTime.of(getLoanResponse.getCutOffDate(), LocalTime.MIN))")
    @Mapping(target = "installmentDate", source = "endDueDate")
    @Mapping(target = "installmentAccrued.value", source = "installmentAccrued.amount")
    @Mapping(target = "installmentAccrued.currency", source = "installmentAccrued.currency")
    @Mapping(target = "installmentExpected.value", source = "installmentExpected.amount")
    @Mapping(target = "installmentExpected.currency", source = "installmentExpected.currency")
    LoanInformation coreBankingInformationTO(GetLoanResponse getLoanResponse);

    @Mapping(target = "cutOffDate", expression = "java(LocalDateTime.of(paymentPlanItem.getCutOffDate(), LocalTime.MIN))")
    PaymentPlan paymentTo(GetLoanResponse.PaymentPlanItem paymentPlanItem);

    @Named("payments")
    default List<PaymentPlan> getPayments(GetLoanResponse getLoanResponse) {
        return getLoanResponse.getPaymentPlanItemApiList()
                .stream()
                .map(INSTANCE::paymentTo)
                .collect(Collectors.toList());
    }

    @Named("rates")
    default Rates getRates(GetLoanResponse getLoanResponse) {
        return Rates.builder()
                .annualEffective(getAnnualEffectiveRateFromAnnualNominalRate(BigDecimal.valueOf(
                        getLoanResponse.getInterestRate())))
                .monthlyNominal(getMonthlyNominalRateFromAnnualNominalRate(BigDecimal.valueOf(
                        getLoanResponse.getInterestRate())))
                .build();
    }
}
