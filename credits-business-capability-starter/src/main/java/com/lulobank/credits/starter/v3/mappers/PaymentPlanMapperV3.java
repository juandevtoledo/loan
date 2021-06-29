package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.sdk.dto.errorv3.ErrorResultV3;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanRequestV3;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanResponseV4;
import com.lulobank.credits.v3.dto.ErrorUseCaseV3;
import com.lulobank.credits.v3.dto.PaymentPlanUseCaseResponseV3;
import com.lulobank.credits.v3.usecase.paymentplan.command.GetPaymentPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentPlanMapperV3 {

    PaymentPlanMapperV3 INSTANCE = Mappers.getMapper(PaymentPlanMapperV3.class);

    @Mapping(target = "idCredit", expression = "java(java.util.UUID.fromString(paymentPlanRequestV3.getIdCredit()))")
    GetPaymentPlan getPaymentPlanFrom(PaymentPlanRequestV3 paymentPlanRequestV3);

    @Mapping(target = "failure", source = "message")
    @Mapping(target = "code", source = "businessCode")
    ErrorResultV3 errorResultV3From(ErrorUseCaseV3 errorUseCaseV3);

    @Deprecated
    PaymentPlanResponseV4 paymentPlanResponseV4From(PaymentPlanUseCaseResponseV3 paymentPlanUseCaseResponseV3);
}
