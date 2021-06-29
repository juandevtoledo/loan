package com.lulobank.credits.v3.usecase.productoffer.mapper;

import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.port.in.productoffer.dto.SimulatedInstallment;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class InstallmentMapperTest {

    @Test
    public void shouldMapOk() {
        FlexibleLoanV3 flexibleLoanV3 = new FlexibleLoanV3();
        flexibleLoanV3.setAmount(BigDecimal.valueOf(150000d));
        flexibleLoanV3.setInstallment(1);

        SimulatedInstallment installment = InstallmentMapper.INSTANCE.toInstallment(flexibleLoanV3);

        assertThat(installment, notNullValue());
        assertThat(installment.getAmount(), is(150000d));
        assertThat(installment.getInstallment(), is(1));
    }

}