package com.lulobank.credits.v3.port.out.corebanking.mapper;

import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper
public interface PaymentPlanMapper {

    PaymentPlanMapper INSTANCE = Mappers.getMapper(PaymentPlanMapper.class);

    @Mapping(target = "installment", source = "counter")
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "pendingBalance", source = "pendingBalance")
    PaymentV3 paymentPlanV3From(SimulatePayment simulatePayment, BigDecimal pendingBalance, Integer counter);

    @AfterMapping
    default void formatDueDate(SimulatePayment simulatePayment, @MappingTarget PaymentV3 paymentV3) {
        paymentV3.setDueDate(simulatePayment.getDueDate().toLocalDate());
    }

    @AfterMapping
    default void setPercents(@MappingTarget PaymentV3 paymentV3) {
        paymentV3.setPercentFeesDue(getPercentByValue(paymentV3, paymentV3.getFeesDue()));
        paymentV3.setPercentPrincipalDue(getPercentByValue(paymentV3, paymentV3.getPrincipalDue()));
        paymentV3.setPercentInterestDue(getPercentByValue(paymentV3, paymentV3.getInterestDue()));
    }

    static float getPercentByValue(@MappingTarget PaymentV3 paymentV3, BigDecimal feesDue) {
        BigDecimal percent = new BigDecimal(100);
        return (feesDue.multiply(percent).divide(paymentV3.getTotalDue(), 2, RoundingMode.CEILING)).floatValue();
    }

}
