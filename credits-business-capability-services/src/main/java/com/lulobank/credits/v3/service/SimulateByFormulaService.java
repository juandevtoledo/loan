package com.lulobank.credits.v3.service;

import com.lulobank.credits.services.utils.InterestUtil;
import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.mapper.OfferEntityV3Mapper;
import com.lulobank.credits.v3.service.dto.FlexibleInstallmentRequest;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@CustomLog
@RequiredArgsConstructor
public class SimulateByFormulaService {

    private final CalculateFlexibleInstallmentService calculateFlexibleInstallmentService;

    public Option<OfferEntityV3> build(OffersTypeV3 offersTypeV3, OfferInformationRequest offerInformationRequest) {
        return Try.of(() -> simulateInstallmentOf48To12(offerInformationRequest))
                .onFailure(error -> log.error("Error trying to simulate installments: {}", error.getMessage()))
                .map(listInstallments -> OfferEntityV3Mapper.INSTANCE.offerEntityV3To(offerInformationRequest, listInstallments, offersTypeV3))
                .toOption();
    }

    private List<FlexibleLoanV3> simulateInstallmentOf48To12(OfferInformationRequest offerInformationRequest) {
        FlexibleInstallmentRequest flexibleInstallmentRequest = getFlexibleInstallmentRequest(offerInformationRequest);
        return calculateFlexibleInstallmentService.generate(flexibleInstallmentRequest);
    }

    private FlexibleInstallmentRequest getFlexibleInstallmentRequest(OfferInformationRequest offerInformationRequest) {
        return FlexibleInstallmentRequest.builder()
                .initialInstallment(12)
                .endInstallment(48)
                .feeInsurance(offerInformationRequest.getFeeInsurance())
                .loanAmount(BigDecimal.valueOf(offerInformationRequest.getLoanAmount()))
                .interestRate(offerInformationRequest.getInterestRate())
                .monthlyNominalRate(InterestUtil.getMonthlyNominalRate(offerInformationRequest.getInterestRate()))
                .annualNominalRate(InterestUtil.getAnnualNominalRate(offerInformationRequest.getInterestRate()))
                .build();
    }

}
