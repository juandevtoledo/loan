package com.lulobank.credits.v3.port.out.corebanking.mapper;

import com.lulobank.credits.v3.port.out.corebanking.dto.GetMovementsRequest;
import com.lulobank.credits.v3.port.out.corebanking.dto.Movement;
import flexibility.client.models.request.GetLoanMovementsRequest;
import flexibility.client.models.response.GetLoanMovementsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

@Mapper
public interface MovementMapper {

    String REPAYMENT= "REPAYMENT";
    String LOAN_REPAYMENT= "LOAN_REPAYMENT";

    MovementMapper INSTANCE = Mappers.getMapper(MovementMapper.class);

    @Mapping(target = "due", source = "valueDate")
    @Mapping(target = "state", source = "transactionType", qualifiedByName = "homologateTransactionType")
    @Mapping(target = "totalDue", source = "amount")
    @Mapping(target = "detail.insuranceAmount", source = "amounts.feesAmount")
    @Mapping(target = "detail.capitalAmount", source = "amounts.principalAmount")
    @Mapping(target = "detail.interestAmount", source = "amounts.interestAmount")
    @Mapping(target = "detail.penaltyAmount", source = "amounts.penaltyAmount")
    Movement getMovementFrom(GetLoanMovementsResponse.LoanMovement loanMovement);

    GetLoanMovementsRequest getLoanMovementsRequestFrom(GetMovementsRequest getMovementsRequest);

    default String homologateTransactionType(String transactionType) {
        return Match(transactionType).of(
                Case($(REPAYMENT), LOAN_REPAYMENT),
                Case($(), transactionType));
    }
}
