package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.starter.v3.adapters.out.dynamo.dto.CreditsDto;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import org.junit.Test;

import java.util.UUID;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_CREDIT;
import static com.lulobank.credits.starter.utils.Samples.creditsEntityBuilder;
import static com.lulobank.credits.starter.utils.Samples.creditsV3EntityBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CreditsEntityMapperTest {

    @Test
    public void toCreditsEntityMapper() {
        CreditsEntity creditsDto = creditsEntityBuilder();
        CreditsV3Entity creditsV3Entity = CreditsEntityMapper.INSTANCE.toCreditsEntity(creditsDto);
        assertThat("IdClient is right", creditsV3Entity.getIdClient(), is(ID_CLIENT));
        assertThat("idCredit is right", creditsV3Entity.getIdCredit(), is(UUID.fromString(ID_CREDIT)));
    }

    @Test
    public void toCreditsDtoMapper() {
        CreditsV3Entity creditsV3Entity =creditsV3EntityBuilder();
        CreditsDto creditsDto = CreditsEntityMapper.INSTANCE.toCreditsDto(creditsV3Entity);
        assertThat("IdClient is right", creditsDto.getIdClient(), is(ID_CLIENT));
        assertThat("idCredit is right", creditsDto.getIdCredit(), is(UUID.fromString(ID_CREDIT)));
    }

    @Test
    public void toCreditMapper() {
        CreditsEntity creditsDto = creditsEntityBuilder();
        CreditsV3Entity creditsV3Entity = CreditsEntityMapper.INSTANCE.toCredit(creditsDto);
        assertThat("IdClient is right", creditsV3Entity.getIdClient(), is(ID_CLIENT));
        assertThat("idCredit is right", creditsV3Entity.getIdCredit(), is(UUID.fromString(ID_CREDIT)));
    }

}
