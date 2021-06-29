package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.ApprovedProductOffer;
import com.lulobank.credits.starter.v3.adapters.in.dto.OfferInstallment;
import org.junit.Test;

import java.util.List;

import static com.lulobank.credits.starter.utils.Samples.buildOffer;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class ApprovedProductOfferMapperTest {

    @Test
    public void shouldMapOk() {
        ApprovedProductOffer productOffer = ApprovedProductOfferMapper.INSTANCE.toApprovedProductOffer(buildOffer());

        assertThat(productOffer, notNullValue());
        assertThat(productOffer.getIdOffer(), notNullValue());
        assertThat(productOffer.getAmount(), is(11000000d));
        assertThat(productOffer.getInterestRate(), is(16.5f));
        assertThat(productOffer.getInsuranceCost(), is(0.0026d));
        assertThat(productOffer.getType(), is("FLEXIBLE_LOAN"));
        assertThat(productOffer.getName(), is("Crédito personalizado"));
        assertThat(productOffer.getMonthlyNominalRate(), is(1.281f));
        assertThat(productOffer.getSimulateInstallment(), hasSize(10));
        assertInstallment(productOffer.getSimulateInstallment());
    }

    private void assertInstallment(List<OfferInstallment> offerInstallment) {
        assertThat(offerInstallment, hasItems(
                samePropertyValuesAs(new OfferInstallment(1, 30000d)),
                samePropertyValuesAs(new OfferInstallment(2, 30000d)),
                samePropertyValuesAs(new OfferInstallment(3, 30000d)),
                samePropertyValuesAs(new OfferInstallment(4, 30000d)),
                samePropertyValuesAs(new OfferInstallment(5, 30000d)),
                samePropertyValuesAs(new OfferInstallment(6, 30000d)),
                samePropertyValuesAs(new OfferInstallment(7, 30000d)),
                samePropertyValuesAs(new OfferInstallment(8, 30000d)),
                samePropertyValuesAs(new OfferInstallment(9, 30000d)),
                samePropertyValuesAs(new OfferInstallment(10, 30000d))
        ));
    }
}