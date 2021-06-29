package com.lulobank.credits.starter;

import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static com.lulobank.credits.starter.ConfigurationTestUtil.buildCreditsEntity;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT_MAMBU;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreditsInboundAdapterTest extends AbstractBaseIntegrationTest {

    private static final String PRODUCT_OFFERED_CLIENT_URL = "/products/v2/offer/client/{idClient}";
    private CreditsEntity creditsEntity;

    @Override
    protected void init() {
        creditsEntity = buildCreditsEntity(ID_CLIENT, ID_CLIENT_MAMBU, UUID.randomUUID().toString());
    }

    @Test
    public void shouldReturnForbiddenWhenIdClientDoesNotMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(PRODUCT_OFFERED_CLIENT_URL, UUID.randomUUID().toString())
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnNotFoundWhenIdClientDoesNotMatchWithEntity() throws Exception {
        when(repository.findByidClient(anyString())).thenReturn(emptyList());
        mockMvc.perform(MockMvcRequestBuilders
                .get(PRODUCT_OFFERED_CLIENT_URL, ID_CLIENT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnOk() throws Exception {
        when(repository.findByidClient(ID_CLIENT)).thenReturn(newArrayList(creditsEntity));
        mockMvc.perform(MockMvcRequestBuilders
                .get(PRODUCT_OFFERED_CLIENT_URL, ID_CLIENT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }
}