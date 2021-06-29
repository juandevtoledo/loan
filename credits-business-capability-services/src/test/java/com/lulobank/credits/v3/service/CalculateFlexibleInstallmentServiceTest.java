package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.service.dto.FlexibleInstallmentRequest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CalculateFlexibleInstallmentServiceTest {

    private CalculateFlexibleInstallmentService calculateFlexibleInstallmentService;

    @Before
    public void setUp() throws Exception {
        calculateFlexibleInstallmentService = new CalculateFlexibleInstallmentService();
    }

    @Test
    public void flexibleLoans_WithOneInstallment() {
        FlexibleInstallmentRequest flexibleInstallmentRequest = getBuildFlexibleInstallment(21, 21,BigDecimal.valueOf(1000000));
        List<FlexibleLoanV3> response = calculateFlexibleInstallmentService.generate(flexibleInstallmentRequest);
        assertThat(response.stream().findFirst().get().getAmount(), is(BigDecimal.valueOf(57132.03)));
        assertThat(response.stream().findFirst().get().getInstallment(), is(21));
        assertThat(response.stream().findFirst().get().getMonthlyNominalRate(), is(BigDecimal.valueOf(1.26666667)));
    }

    @Test
    public void flexibleLoans_From15Installment() {
        FlexibleInstallmentRequest flexibleInstallmentRequest = getBuildFlexibleInstallment(15,15,BigDecimal.valueOf(1000000));
        List<FlexibleLoanV3> response = calculateFlexibleInstallmentService.generate(flexibleInstallmentRequest);
        assertThat(response.stream().findFirst().get().getAmount(), is(BigDecimal.valueOf(76220.51)));
    }


    @Test
    public void flexibleLoans_From13Installment() {
        FlexibleInstallmentRequest flexibleInstallmentRequest = getBuildFlexibleInstallment(13,13,BigDecimal.valueOf(1000000));
        List<FlexibleLoanV3> response = calculateFlexibleInstallmentService.generate(flexibleInstallmentRequest);
        assertThat(response.stream().findFirst().get().getAmount(), is(BigDecimal.valueOf(86515.21)));
    }


    @Test
    public void flexibleLoans_From12to24Installment() {
        FlexibleInstallmentRequest flexibleInstallmentRequest = getBuildFlexibleInstallment(12, 24,BigDecimal.valueOf(1000000));
        List<FlexibleLoanV3> response = calculateFlexibleInstallmentService.generate(flexibleInstallmentRequest);
        assertThat(response.size(), is(13));
    }

    @Test
    public void flexibleLoans_WhenRequestInvalid() {
        FlexibleInstallmentRequest flexibleInstallmentRequest = getBuildFlexibleInstallment(0, 0,BigDecimal.ZERO);
        List<FlexibleLoanV3> response = calculateFlexibleInstallmentService.generate(flexibleInstallmentRequest);
        assertThat(response.isEmpty(), is(true));
    }


    private FlexibleInstallmentRequest getBuildFlexibleInstallment(int initInstallment,int endInstallment, BigDecimal amount) {
        return FlexibleInstallmentRequest.builder()
                .monthlyNominalRate(BigDecimal.valueOf(1.26666667))
                .feeInsurance(0.0026)
                .endInstallment(endInstallment)
                .initialInstallment(initInstallment)
                .loanAmount(amount)
                .build();
    }
}
