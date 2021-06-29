package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.Schedule;
import com.lulobank.credits.v3.mapper.OfferEntityV3Mapper;
import com.lulobank.credits.v3.service.dto.FlexibleInstallmentRequest;
import com.lulobank.credits.v3.service.dto.SimulateSchedule;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lulobank.credits.v3.service.dto.SimulateSchedule.empty;
import static java.util.stream.Collectors.toList;

@CustomLog
@RequiredArgsConstructor
public class SimulateByRiskResponse {

    private final CalculateFlexibleInstallmentService calculateFlexibleInstallmentService;

    public Option<OfferEntityV3> build(Double feeInsurance, BigDecimal clientLoanRequestedAmount, List<Schedule> schedules) {
        return Try.of(() -> getFlexibleInstallments(feeInsurance, clientLoanRequestedAmount, getOrderSchedules(schedules)))
                .onFailure(error -> log.error("Error trying to simulate installments: {}", error.getMessage(), error))
                .map(flexibleLoanV3s -> OfferEntityV3Mapper.INSTANCE.offerEntityV3To(clientLoanRequestedAmount, flexibleLoanV3s, OffersTypeV3.FLEXIBLE_LOAN, feeInsurance))
                .toOption();
    }

    private List<FlexibleLoanV3> getFlexibleInstallments(Double feeInsurance, BigDecimal clientLoanRequestedAmount, List<Schedule> schedules) {
        return getSimulateSchedules(schedules)
                .parallelStream()
                .flatMap(schedule -> simulateFlexibleInstallment(schedule, feeInsurance, clientLoanRequestedAmount))
                .collect(toList());

    }

    private Stream<FlexibleLoanV3> simulateFlexibleInstallment(SimulateSchedule schedule, Double feeInsurance, BigDecimal loanAmount) {
        return calculateFlexibleInstallmentService.generate(getFlexibleInstallmentRequest(schedule, feeInsurance, loanAmount))
                .stream()
                .sorted(Comparator.comparingInt(FlexibleLoanV3::getInstallment));
    }

    private List<SimulateSchedule> getSimulateSchedules(List<Schedule> schedules) {

        return io.vavr.collection.Stream.iterate(1, sch -> sch + 1)
                .takeWhile(sch -> sch < schedules.size())
                .map(sch -> toSimulateSchedule(schedules.get(sch), getInitialInstallment(schedules, sch)))
                .insert(0, firstSchedules(schedules))
                .toJavaList();

    }

    private int getInitialInstallment(List<Schedule> schedules, Integer sch) {
        return schedules.get(sch - 1).getInstallment()+1;
    }

    private SimulateSchedule firstSchedules(List<Schedule> schedules) {
        return Option.ofOptional(schedules.stream().findFirst())
                .map(schedule ->toSimulateSchedule(schedule,schedule.getInstallment()))
                .getOrElse(empty());
    }

    private SimulateSchedule toSimulateSchedule(Schedule schedule, Integer initialInstallment) {
        return SimulateSchedule.builder()
                .interestRate(schedule.getInterestRate())
                .monthlyNominalRate(schedule.getMonthlyNominalRate())
                .annualNominalRate(schedule.getAnnualNominalRate())
                .intInstallment(initialInstallment)
                .endInstallment(schedule.getInstallment())
                .build();
    }


    private FlexibleInstallmentRequest getFlexibleInstallmentRequest(SimulateSchedule schedule, Double feeInsurance, BigDecimal loanAmount) {
        return FlexibleInstallmentRequest.builder()
                .initialInstallment(schedule.getIntInstallment())
                .endInstallment(schedule.getEndInstallment())
                .feeInsurance(feeInsurance)
                .loanAmount(loanAmount)
                .interestRate(schedule.getInterestRate())
                .monthlyNominalRate(schedule.getMonthlyNominalRate())
                .annualNominalRate(schedule.getAnnualNominalRate())
                .build();
    }

    private List<Schedule> getOrderSchedules(List<Schedule> schedules) {
        return schedules.stream().sorted(Comparator.comparingInt(Schedule::getInstallment)).collect(Collectors.toList());
    }
}
