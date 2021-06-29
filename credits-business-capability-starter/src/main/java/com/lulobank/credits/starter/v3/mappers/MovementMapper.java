package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.movement.Movement;
import com.lulobank.credits.starter.v3.util.TimeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = {TimeConverter.class})
public interface MovementMapper {

    MovementMapper INSTANCE = Mappers.getMapper(MovementMapper.class);

    @Mapping(target = "totalDue.value", source = "totalDue.amount.roundValue")
    @Mapping(target = "totalDue.currency", source = "totalDue.currency.value")
    @Mapping(target = "detail.insuranceAmount.value", source = "detail.insuranceAmount.amount.roundValue")
    @Mapping(target = "detail.insuranceAmount.currency", source = "detail.insuranceAmount.currency.value")
    @Mapping(target = "detail.capitalAmount.value", source = "detail.capitalAmount.amount.roundValue")
    @Mapping(target = "detail.capitalAmount.currency", source = "detail.capitalAmount.currency.value")
    @Mapping(target = "detail.interestAmount.value", source = "detail.interestAmount.amount.roundValue")
    @Mapping(target = "detail.interestAmount.currency", source = "detail.interestAmount.currency.value")
    @Mapping(target = "detail.penaltyAmount.value", source = "detail.penaltyAmount.amount.roundValue")
    @Mapping(target = "detail.penaltyAmount.currency", source = "detail.penaltyAmount.currency.value")
    @Mapping(target = "due", expression = "java(TimeConverter.toUTC(movement.getDue()))")
    Movement movementFrom(com.lulobank.credits.v3.usecase.movement.dto.Movement movement);

    List<Movement> movementsFrom(List<com.lulobank.credits.v3.usecase.movement.dto.Movement> movements);
}
