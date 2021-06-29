package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.service.dto.FlexibleInstallmentRequest;
import com.lulobank.credits.v3.util.RoundNumber;
import io.vavr.collection.Stream;
import lombok.CustomLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.math.BigDecimal.ONE;

@CustomLog
public class CalculateFlexibleInstallmentService {

    public static final int SCALE_FORMULA = 12;

    public List<FlexibleLoanV3> generate(FlexibleInstallmentRequest flexibleInstallmentRequest) {
        return Stream.iterate(flexibleInstallmentRequest.getEndInstallment(), installment -> installment - 1)
                .takeWhile(installment -> installment >= flexibleInstallmentRequest.getInitialInstallment() && installment > 0)
                .map(installment -> createFlexibleOffer(installment, flexibleInstallmentRequest))
                .toJavaList();
    }

    private FlexibleLoanV3 createFlexibleOffer(Integer installment, FlexibleInstallmentRequest flexibleInstallmentRequest) {
        FlexibleLoanV3 flexibleLoanV3 = new FlexibleLoanV3();
        flexibleLoanV3.setAmount(getAmountInstallment(installment, flexibleInstallmentRequest));
        flexibleLoanV3.setInstallment(installment);
        flexibleLoanV3.setMonthlyNominalRate(flexibleInstallmentRequest.getMonthlyNominalRate());
        flexibleLoanV3.setInterestRate(flexibleInstallmentRequest.getInterestRate());
        flexibleLoanV3.setAnnualNominalRate(flexibleInstallmentRequest.getAnnualNominalRate());
        return flexibleLoanV3;
    }

    /**
     * Function to calculate monthly amount installments based by PMD Excel Functions
     * a = VP * ((i*(i+1)"/((i+1)-1)) + feeInsurance
     */
    private BigDecimal getAmountInstallment(Integer installment, FlexibleInstallmentRequest flexibleInstallmentRequest) {
        BigDecimal r = getPercentageRate(flexibleInstallmentRequest);
        BigDecimal v = ONE.add(r).pow(installment);
        BigDecimal division = r.multiply(v).divide(v.subtract(ONE), SCALE_FORMULA,RoundingMode.HALF_UP);
        BigDecimal a = flexibleInstallmentRequest.getLoanAmount().multiply(division);
        BigDecimal moreInsurance = a.add(getInsuranceCost(flexibleInstallmentRequest));
        return RoundNumber.defaultScale(moreInsurance);
    }

    private BigDecimal getPercentageRate(FlexibleInstallmentRequest flexibleInstallmentRequest) {
        return flexibleInstallmentRequest.getMonthlyNominalRate().divide(BigDecimal.valueOf(100), SCALE_FORMULA, RoundingMode.HALF_UP);
    }

    /**
     * Function to calculate insurance based by PMD Excel Functions
     * Formula = amount * fee
     */
    private BigDecimal getInsuranceCost(FlexibleInstallmentRequest flexibleInstallmentRequest) {
        return flexibleInstallmentRequest.getLoanAmount().multiply(BigDecimal.valueOf(flexibleInstallmentRequest.getFeeInsurance()));
    }
}
