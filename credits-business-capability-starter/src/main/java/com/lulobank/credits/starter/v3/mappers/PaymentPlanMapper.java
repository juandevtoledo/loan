package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.PaymentPlanResponse;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentPlanMapper {

    PaymentPlanMapper INSTANCE = Mappers.getMapper(PaymentPlanMapper.class);

    PaymentPlanResponse paymentPlanResponseFrom(PaymentPlanUseCaseResponse paymentPlanUseCaseResponse);
}
