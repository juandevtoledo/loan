package com.lulobank.credits.v3.usecase.paymentplan.mapper;

import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.usecase.paymentplan.command.GetPaymentPlan;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;
import io.vavr.control.Option;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.lulobank.credits.v3.dto.FlexibleLoanV3.empty;
import static com.lulobank.credits.v3.util.RoundNumber.defaultScale;

public class PaymentPlanUseCaseResponseMapper {

    private PaymentPlanUseCaseResponseMapper() {
    }

    public static PaymentPlanUseCaseResponse paymentPlanUseCaseResponseMapperFrom(OfferEntityV3 offerEntityV3, List<PaymentV3> paymentPlanV3s, GetPaymentPlan command) {
        FlexibleLoanV3 flexibleLoan = getFlexibleLoanByInstallment(offerEntityV3, command.getInstallments());
        return PaymentPlanUseCaseResponse.builder()
                .principalDebit(BigDecimal.valueOf(offerEntityV3.getAmount()))
                .interestRate(defaultScale(flexibleLoan.getInterestRate()))
                .monthlyNominalRate(defaultScale(flexibleLoan.getMonthlyNominalRate()))
                .startDate(LocalDate.now())
                .endDate(Option.of(io.vavr.collection.List.ofAll(paymentPlanV3s).last()).map(PaymentV3::getDueDate).getOrNull())
                .paymentPlan(paymentPlanV3s)
                .build();
    }

    private static FlexibleLoanV3 getFlexibleLoanByInstallment(OfferEntityV3 offerEntityV3, Integer installment) {
        return Option.of(offerEntityV3.getFlexibleLoans())
                .flatMap(flexibleLoanV3s ->
                        Option.ofOptional(flexibleLoanV3s.stream()
                                .filter(flexibleLoan -> installment.equals(flexibleLoan.getInstallment()))
                                .findFirst())
                ).getOrElse(empty());
    }


}
