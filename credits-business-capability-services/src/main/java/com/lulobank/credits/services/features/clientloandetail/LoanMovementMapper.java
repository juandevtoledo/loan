package com.lulobank.credits.services.features.clientloandetail;

import com.lulobank.credits.sdk.dto.clientloandetail.Payment;
import flexibility.client.models.response.GetLoanMovementsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoanMovementMapper {

    LoanMovementMapper INSTANCE = Mappers.getMapper(LoanMovementMapper.class);

    @Mapping(target = "totalDue", source = "amount")
    @Mapping(target = "state", source = "transactionType")
    @Mapping(target = "due", expression = "java(loanMovement.getCreationDate().toLocalDate())")
    @Mapping(target = "detail.insuranceCost", source = "amounts.feesAmount")
    @Mapping(target = "detail.capitalPayment", source = "amounts.principalAmount")
    @Mapping(target = "detail.ratePayment", source = "amounts.interestAmount")
    @Mapping(target = "detail.penaltyAmount", source = "amounts.penaltyAmount")
    Payment paymentTo(GetLoanMovementsResponse.LoanMovement loanMovement);
}
