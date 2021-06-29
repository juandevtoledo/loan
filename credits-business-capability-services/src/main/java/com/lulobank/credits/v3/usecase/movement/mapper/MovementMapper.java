package com.lulobank.credits.v3.usecase.movement.mapper;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.corebanking.dto.GetMovementsRequest;
import com.lulobank.credits.v3.usecase.movement.dto.Movement;
import com.lulobank.credits.v3.vo.loan.CurrencyVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = {CurrencyVO.class})
public interface MovementMapper {

    String DEFAULT_CURRENCY_EXPR = "java(CurrencyVO.defaultCurrency())";

    MovementMapper INSTANCE = Mappers.getMapper(MovementMapper.class);

    @Mapping(target = "totalDue.amount.value", source = "totalDue")
    @Mapping(target = "totalDue.currency", expression = DEFAULT_CURRENCY_EXPR)
    @Mapping(target = "detail.insuranceAmount.amount.value", source = "detail.insuranceAmount")
    @Mapping(target = "detail.insuranceAmount.currency", expression = DEFAULT_CURRENCY_EXPR)
    @Mapping(target = "detail.capitalAmount.amount.value", source = "detail.capitalAmount")
    @Mapping(target = "detail.capitalAmount.currency", expression = DEFAULT_CURRENCY_EXPR)
    @Mapping(target = "detail.interestAmount.amount.value", source = "detail.interestAmount")
    @Mapping(target = "detail.interestAmount.currency", expression = DEFAULT_CURRENCY_EXPR)
    @Mapping(target = "detail.penaltyAmount.amount.value", source = "detail.penaltyAmount")
    @Mapping(target = "detail.penaltyAmount.currency", expression = DEFAULT_CURRENCY_EXPR)
    Movement movementFrom(com.lulobank.credits.v3.port.out.corebanking.dto.Movement movement);

    List<Movement> movementsFrom(List<com.lulobank.credits.v3.port.out.corebanking.dto.Movement> movements);

    @Mapping(target = "loanNumber", source = "credit.idLoanAccountMambu")
    @Mapping(target = "clientId", source = "credit.idClientMambu")
    GetMovementsRequest getMovementsRequestFrom(CreditsV3Entity credit);
}
