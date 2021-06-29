package com.lulobank.credits.v3.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.credits.v3.util.EntitiesFactory;
import io.vavr.control.Option;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OfferServiceTest {

    @Test
    public void offerEntityWithOfferValid() throws JsonProcessingException {
        ObjectMapper objectMapper= new ObjectMapper();
        OfferService offerService =new OfferService();
        AcceptOffer acceptOffer= EntitiesFactory.AcceptOfferFactory.createAcceptOfferWithOfferValid();
        Option<OfferEntityV3> offerEntityV3 =offerService.getOffer(EntitiesFactory.CreditsEntityFactory.foundCreditsEntityInBD(),acceptOffer);
        System.out.println(objectMapper.writeValueAsString(offerEntityV3.get()));
        assertThat(offerEntityV3.isDefined(), is(true));
        assertThat(offerEntityV3.get().getAmountInstallment().equals(acceptOffer.getSelectedCredit().getAmountInstallment()), is(true));
        assertThat(offerEntityV3.get().getInstallments().equals(acceptOffer.getSelectedCredit().getInstallments()), is(true));

    }

    @Test
    public void offerEntityWithOfferInvalid() {

        OfferService offerService =new OfferService();
        Option<OfferEntityV3> offerEntityV3 =offerService.getOffer(EntitiesFactory.CreditsEntityFactory.foundCreditsEntityInBD(),EntitiesFactory.AcceptOfferFactory.createAcceptOfferWithOfferInvalid());
        assertThat(offerEntityV3.isDefined(), is(false));
    }
}
