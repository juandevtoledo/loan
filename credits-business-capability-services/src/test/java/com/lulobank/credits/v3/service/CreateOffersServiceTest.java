package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.lulobank.credits.v3.util.EntitiesFactory.OfferFactory.createOfferEntityV3Valid;
import static com.lulobank.credits.v3.util.EntitiesFactory.OfferInformationRequestFactory.OfferInformationRequestBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CreateOffersServiceTest {

    @Mock
    private SimulateService simulateService;
    @Mock
    private SimulateByFormulaService simulateByFormulaService;
    private CreateOffersService createOffersService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        createOffersService = new CreateOffersService(simulateByFormulaService, simulateService);
    }

    @Test
    public void getOffersOk() {
        OfferEntityV3 offerEntityV3 = createOfferEntityV3Valid();
        when(simulateService.createOffer(any(), any())).thenReturn(Option.of(offerEntityV3));
        when(simulateByFormulaService.build(any(), any())).thenReturn(Option.of(createOfferEntityV3Valid()));
        List<OfferEntityV3> list = createOffersService.calculate(OfferInformationRequestBuilder().build());
        assertThat(list.isEmpty(), is(false));
        assertThat(list.size(), is(3));
    }

    @Test
    public void offersNotGenerateSinceErrorService() {
        when(simulateService.createOffer(any(), any())).thenReturn(Option.none());
        when(simulateByFormulaService.build(any(), any())).thenReturn(Option.none());
        List<OfferEntityV3> list = createOffersService.calculate(OfferInformationRequestBuilder().build());
        assertThat(list.isEmpty(), is(true));
    }
}
