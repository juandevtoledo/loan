package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import org.junit.Test;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_CREDIT;
import static com.lulobank.credits.starter.utils.Samples.creditWithOfferV3RequestBuilder;
import static org.junit.Assert.assertEquals;

public class CreditWithOfferMapperTest {

    @Test
    public void mapper(){
        AcceptOffer acceptOffer=CreditWithOfferMapper.INSTANCE.toAcceptOffer(creditWithOfferV3RequestBuilder(),ID_CLIENT);
        assertEquals("IdClient is right", acceptOffer.getIdClient(), ID_CLIENT);
        assertEquals("IdCredit is right", acceptOffer.getIdCredit(), ID_CREDIT);
    }

}
