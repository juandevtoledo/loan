package com.lulobank.credits.starter.v3.adapters.out.loan.mapper;

import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePayment;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePaymentRequest;
import flexibility.client.models.request.SimulatedLoanRequest;
import flexibility.client.models.response.SimulatedLoanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;



@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SimulateLoanMapper {

    SimulateLoanMapper INSTANCE = Mappers.getMapper(SimulateLoanMapper.class);

    @Mapping(target = "repaymentInstallments",source = "simulatePaymentRequest.installment")
    @Mapping(target = "amount.amount",source = "simulatePaymentRequest.amount" )
    @Mapping(target = "amount.currency",source = "creditsConditionV3.defaultCurrency")
    @Mapping(target = "paymentDay",source = "simulatePaymentRequest.dayOfPay")
    SimulatedLoanRequest simulateLoanRequestFrom(SimulatePaymentRequest simulatePaymentRequest, CreditsConditionV3 creditsConditionV3);

    @Mapping(target = "feesDue",source = "insuranceFee")
    SimulatePayment simulatePaymentsFrom(SimulatedLoanResponse.Repayment repayments);


}
