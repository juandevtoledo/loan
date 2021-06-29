package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.PaymentPlanUseCaseResponseV3;
import com.lulobank.credits.v3.dto.PaymentV3;
import io.vavr.control.Option;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Deprecated
@Mapper
public interface PaymentPlanUseCaseResponseV3Mapper {

    PaymentPlanUseCaseResponseV3Mapper INSTANCE = Mappers.getMapper(PaymentPlanUseCaseResponseV3Mapper.class);

    @Mapping(target = "principalDebit", source = "offerEntityV3.amount")
    @Mapping(target = "annualNominalRate", source = "offerEntityV3.interestRate")
    PaymentPlanUseCaseResponseV3 paymentPlanUseCaseResponseV3MapperFrom(OfferEntityV3 offerEntityV3, List<PaymentV3> paymentPlanV3s);

    @AfterMapping
    default void datePayments(List<PaymentV3> paymentPlanV3s, @MappingTarget PaymentPlanUseCaseResponseV3 paymentPlanUseCaseResponseV3) {
        paymentPlanUseCaseResponseV3.setStartDate(Option.ofOptional(paymentPlanV3s.stream().findFirst()).map(PaymentV3::getDueDate).getOrNull());
        paymentPlanUseCaseResponseV3.setEndDate(Option.of(io.vavr.collection.List.ofAll(paymentPlanV3s).last()).map(PaymentV3::getDueDate).getOrNull());
        paymentPlanUseCaseResponseV3.setPaymentPlan(paymentPlanV3s);
    }
}
