package com.lulobank.credits.v3.usecase.paymentplan.mapper;

import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentPlan;
import com.lulobank.credits.v3.port.out.corebanking.mapper.PaymentPlanItemMapper;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityInstallmentPendingStateEnum.LATE;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityInstallmentPendingStateEnum.PARTIALLY_PAID;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityInstallmentPendingStateEnum.PENDING;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityInstallmentPendingStateEnum.RESCHEDULED;

public class LoanPaymentPlanUseCaseMapper {

    private LoanPaymentPlanUseCaseMapper() {
    }

    public static PaymentPlanUseCaseResponse paymentPlanUseCaseResponseMapperFrom(LoanInformation loanInformation) {
        BigDecimal totalBalanceExpected = getTotalBalanceExpected(loanInformation.getPaymentPlanList());
        return PaymentPlanUseCaseResponse.builder()
                .principalDebit(loanInformation.getLoanAmount().getValue())
                .totalBalance(loanInformation.getTotalBalance().getValue())
                .totalBalanceExpected(totalBalanceExpected)
                .interestRate(loanInformation.getRates().getAnnualEffective())
                .monthlyNominalRate(loanInformation.getRates().getMonthlyNominal())
                .startDate(loanInformation.getCreationOn().toLocalDate())
                .endDate(getEndDate(loanInformation.getPaymentPlanList()))
                .paymentPlan(toPaymentPlan(loanInformation.getPaymentPlanList(),
                        new AtomicReference<>(totalBalanceExpected),
                        new AtomicInteger(1)))
                .build();
    }

    private static List<PaymentV3> toPaymentPlan(List<PaymentPlan> paymentPlan, AtomicReference<BigDecimal> pendingBalAccumulate, AtomicInteger counter) {
        return paymentPlan.stream()
                .map(paymentPlanItem -> {
                    BigDecimal pendingBalance = pendingBalAccumulate.accumulateAndGet(paymentPlanItem.getTotalDue(), BigDecimal::subtract);
                    return PaymentPlanItemMapper.INSTANCE.paymentPlanV3From(paymentPlanItem, pendingBalance, counter.getAndIncrement());
                }).collect(Collectors.toList());
    }

    private static LocalDate getEndDate(List<PaymentPlan> paymentPlan) {
        return io.vavr.collection.List.ofAll(paymentPlan)
                .filter(inState(PENDING.name(), PARTIALLY_PAID.name(), LATE.name(), RESCHEDULED.name()))
                .last().getDueDate().toLocalDate();
    }

    private static BigDecimal getTotalBalanceExpected(List<PaymentPlan> paymentPlan) {
        return paymentPlan.stream()
                .map(PaymentPlan::getTotalDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Predicate<PaymentPlan> inState(String... states) {
        return paymentPlanItem -> Arrays.asList(states).contains(paymentPlanItem.getState());
    }
}
