package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePayment;
import io.vavr.control.Option;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentPlanV3Mapper {

    PaymentPlanV3Mapper INSTANCE = Mappers.getMapper(PaymentPlanV3Mapper.class);

    @Mapping(target = "installment", source = "counter")
    @Mapping(target = "dueDate", ignore = true)
    PaymentV3 paymentPlanV3From(SimulatePayment simulatePayment, Integer counter);

    @AfterMapping
    default void setPercents(@MappingTarget PaymentV3 paymentV3) {
        paymentV3.setPercentFeesDue(getPercentByValue(paymentV3, paymentV3.getFeesDue()));
        paymentV3.setPercentPrincipalDue(getPercentByValue(paymentV3, paymentV3.getPrincipalDue()));
        paymentV3.setPercentInterestDue(getPercentByValue(paymentV3, paymentV3.getInterestDue()));
    }

    @AfterMapping
    default void formatDueDate(SimulatePayment simulatePayment, @MappingTarget PaymentV3 paymentV3) {
        paymentV3.setDueDate(
                Option.of(simulatePayment)
                .map(SimulatePayment::getDueDate)
                .map(LocalDateTime::toLocalDate)
                .getOrNull()
        );
    }

    static float getPercentByValue(@MappingTarget PaymentV3 paymentV3, BigDecimal feesDue) {
        BigDecimal percent = new BigDecimal(100);
        return (feesDue.multiply(percent).divide(paymentV3.getTotalDue(), 2, RoundingMode.CEILING)).floatValue();
    }
}
