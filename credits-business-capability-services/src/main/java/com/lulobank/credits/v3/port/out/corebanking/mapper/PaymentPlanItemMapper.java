package com.lulobank.credits.v3.port.out.corebanking.mapper;

import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentPlan;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ZERO;

@Mapper
public interface PaymentPlanItemMapper {

    PaymentPlanItemMapper INSTANCE = Mappers.getMapper(PaymentPlanItemMapper.class);

    @Mapping(target = "installment", source = "counter")
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "cutOffDate", ignore = true)
    @Mapping(target = "pendingBalance", source = "pendingBalance")
    PaymentV3 paymentPlanV3From(PaymentPlan paymentPlan, BigDecimal pendingBalance, Integer counter);

    @AfterMapping
    default void formatDates(PaymentPlan paymentPlan, @MappingTarget PaymentV3 paymentV3) {
        paymentV3.setDueDate(paymentPlan.getDueDate().toLocalDate());
        paymentV3.setCutOffDate(paymentPlan.getCutOffDate().toLocalDate());
    }

    @AfterMapping
    default void setPercents(@MappingTarget PaymentV3 paymentV3) {
        paymentV3.setPercentFeesDue(getPercentByValue(paymentV3, paymentV3.getFeesDue()));
        paymentV3.setPercentPrincipalDue(getPercentByValue(paymentV3, paymentV3.getPrincipalDue()));
        paymentV3.setPercentInterestDue(getPercentByValue(paymentV3, paymentV3.getInterestDue()));
        paymentV3.setPercentPenaltyDue(getPercentByValue(paymentV3, paymentV3.getPenaltyDue()));
    }

    static float getPercentByValue(@MappingTarget PaymentV3 paymentV3, BigDecimal feesDue) {
        BigDecimal percent = new BigDecimal(100);
        return paymentV3.getTotalDue().compareTo(ZERO)  == 0 ? ZERO.floatValue() :
                (feesDue.multiply(percent).divide(paymentV3.getTotalDue(), 2, RoundingMode.HALF_UP)).floatValue();
    }
}
