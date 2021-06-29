package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.OfferInstallment;
import com.lulobank.credits.v3.port.in.productoffer.dto.SimulatedInstallment;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class OfferInstallmentMapperTest {

    @Test
    public void shouldMapOk() {
        SimulatedInstallment simulatedInstallment = new SimulatedInstallment(1, 650000d);
        OfferInstallment installment = OfferInstallmentMapper.INSTANCE.toOfferInstallment(simulatedInstallment);

        assertThat(installment, notNullValue());
        assertThat(installment.getInstallment(), is(1));
        assertThat(installment.getAmount(), is(650000d));
    }
}