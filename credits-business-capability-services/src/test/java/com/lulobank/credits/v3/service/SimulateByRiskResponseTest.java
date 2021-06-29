package com.lulobank.credits.v3.service;

import com.lulobank.credits.services.utils.InterestUtil;
import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.Schedule;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SimulateByRiskResponseTest {

    public static final double FEE_INSURANCE = 0.0026;
    public static final Double AMOUNT = 2000000d;
    private SimulateByRiskResponse simulateByRiskResponse;

    @Before
    public void setUp() throws Exception {
        simulateByRiskResponse = new SimulateByRiskResponse(new CalculateFlexibleInstallmentService());
    }

    @Test
    public void OfferEntityV3_WhenSchedulesIsValid() {
        List<Schedule> schedules = schedules();
        Option<OfferEntityV3> response = simulateByRiskResponse.build(FEE_INSURANCE, BigDecimal.valueOf(AMOUNT), schedules);
        assertThatFlebleLoansValid(response);
        assertThat(response.get().getType(), is(OffersTypeV3.FLEXIBLE_LOAN.name()));
        assertThat(response.get().getAmount(), is(AMOUNT));
    }


    @Test
    public void OfferEntityV3_WhenSchedulesIsEmpty() {
        Option<OfferEntityV3> response = simulateByRiskResponse.build(FEE_INSURANCE, BigDecimal.valueOf(AMOUNT), Collections.EMPTY_LIST);
        assertThat(flexibleLoans(response).isEmpty(), is(true));
    }


    private List<Schedule> schedules() {
        List<Schedule> scheduleList = new ArrayList<>();
        scheduleList.add(geSchedule(BigDecimal.valueOf(16.3f), 24));
        scheduleList.add(geSchedule(BigDecimal.valueOf(16.55f), 12));
        scheduleList.add(geSchedule(BigDecimal.valueOf(15f), 36));
        scheduleList.add(geSchedule(BigDecimal.valueOf(14.82f), 48));
        return scheduleList;
    }


    private void assertThatFlebleLoansValid(Option<OfferEntityV3> response) {
        assertThat(flexibleLoans(response).size(), is(37));
        assertThat(flexibleLoans(response).stream().findFirst().get().getMonthlyNominalRate(), is(BigDecimal.valueOf(1.28416667)));
        assertThat(flexibleLoans(response).stream().findFirst().get().getInstallment(), is(12));
        assertThat(flexibleLoans(response).stream().findFirst().get().getAmount(), is(BigDecimal.valueOf(186103.79)));
    }

    private List<FlexibleLoanV3> flexibleLoans(Option<OfferEntityV3> response) {
        return response.get().getFlexibleLoans();
    }

    private Schedule geSchedule(BigDecimal interestRate, Integer installment) {
        return Schedule.builder()
                .interestRate(interestRate)
                .installment(installment)
                .monthlyNominalRate(InterestUtil.getMonthlyNominalRate(interestRate))
                .annualNominalRate(InterestUtil.getAnnualNominalRate(interestRate))
                .build();
    }


}


