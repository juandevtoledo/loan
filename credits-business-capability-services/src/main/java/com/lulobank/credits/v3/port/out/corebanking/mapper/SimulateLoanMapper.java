package com.lulobank.credits.v3.port.out.corebanking.mapper;

import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePaymentRequest;
import com.lulobank.credits.v3.service.OffersTypeV3;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.usecase.paymentplan.command.GetPaymentPlan;
import io.vavr.control.Option;

import java.math.BigDecimal;

import static com.lulobank.credits.services.utils.InterestUtil.getAnnualNominalRate;
import static java.math.BigDecimal.ZERO;

public class SimulateLoanMapper {

    private SimulateLoanMapper() {
    }

    public static SimulatePaymentRequest simulatePaymentRequestFrom(OfferEntityV3 offerEntityV3, GetPaymentPlan getPaymentPlan) {
        return SimulatePaymentRequest.builder()
                .installment(getPaymentPlan.getInstallments())
                .interestRate(getAnnualInterestRateByInstallment(offerEntityV3, getPaymentPlan.getInstallments()))
                .dayOfPay(getPaymentPlan.getDayOfPay())
                .amount(BigDecimal.valueOf(offerEntityV3.getAmount()))
                .build();
    }

    public static SimulatePaymentRequest simulatePaymentRequestFrom(OfferInformationRequest offerInformationRequest, OffersTypeV3 offersTypeV3) {
        return SimulatePaymentRequest.builder()
                .amount(BigDecimal.valueOf(offerInformationRequest.getLoanAmount()))
                .installment(offersTypeV3.getInstallment())
                .interestRate(getAnnualNominalRate(offerInformationRequest.getInterestRate()))
                .build();
    }

    private static BigDecimal getAnnualInterestRateByInstallment(OfferEntityV3 offerEntityV3, Integer installment) {
        return Option.of(offerEntityV3.getFlexibleLoans())
                .map(flexibleLoanV3s ->
                        flexibleLoanV3s.stream()
                                .filter(flexibleLoan -> installment.equals(flexibleLoan.getInstallment()))
                                .map(FlexibleLoanV3::getAnnualNominalRate)
                                .findFirst()
                                .orElse(ZERO)
                ).getOrElse(ZERO);
    }

}
