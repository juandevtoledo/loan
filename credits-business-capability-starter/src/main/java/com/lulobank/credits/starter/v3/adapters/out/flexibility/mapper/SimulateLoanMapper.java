package com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper;

import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePaymentRequest;
import flexibility.client.models.request.SimulatedLoanRequest;
import flexibility.client.models.response.SimulatedLoanResponse;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SimulateLoanMapper {

    SimulateLoanMapper INSTANCE = Mappers.getMapper(SimulateLoanMapper.class);

    @Mapping(target = "repaymentInstallments", source = "simulatePaymentRequest.installment")
    @Mapping(target = "amount.amount", source = "simulatePaymentRequest.amount")
    @Mapping(target = "amount.currency", source = "creditsConditionV3.defaultCurrency")
    @Mapping(target = "paymentDay", source = "simulatePaymentRequest.dayOfPay")
    SimulatedLoanRequest simulateLoanRequestFrom(SimulatePaymentRequest simulatePaymentRequest, CreditsConditionV3 creditsConditionV3);

    @IterableMapping(qualifiedByName = "toSimulatePayment")
    List<SimulatePayment> simulatePaymentsFrom(List<SimulatedLoanResponse.Repayment> repayments);

    @Mapping(target = "feesDue", source = "insuranceFee")
    SimulatePayment simulatePaymentFrom(SimulatedLoanResponse.Repayment repayments);

    @Named("toSimulatePayment")
    default SimulatePayment toSimulatePayment(SimulatedLoanResponse.Repayment repayment) {
        return INSTANCE.simulatePaymentFrom(repayment);
    }

}
