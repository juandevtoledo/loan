package com.lulobank.credits.v3.usecase.intialsoffersv3;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.service.CreateOffersService;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.usecase.intialsoffersv3.command.GetOffersByClient;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static com.lulobank.credits.v3.usecase.intialsoffersv3.OfferResponseV3.CO;
import static com.lulobank.credits.v3.usecase.intialsoffersv3.OfferResponseV3.KO;
import static com.lulobank.credits.v3.usecase.intialsoffersv3.OfferResponseV3.OK;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static com.lulobank.credits.v3.util.EntitiesFactory.InitialsOfferFactory.initialsOfferRequest;
import static com.lulobank.credits.v3.util.EntitiesFactory.OfferFactory.createOfferEntityV3Valid;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class InitialsOffersV3UseCaseTest {

    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private CreateOffersService createOffersService;
    @Captor
    private ArgumentCaptor<CreditsV3Entity> creditsV3ArgumentCaptor;
    @Captor
    private ArgumentCaptor<OfferInformationRequest> offerInformationRequestCaptor;
    private InitialsOffersV3UseCase initialsOffersV3UseCaseTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        initialsOffersV3UseCaseTest = new InitialsOffersV3UseCase(creditsV3Repository, createOffersService, 0.0034);
    }

    @Test
    public void GenerateOffersOk() {
        GetOffersByClient getOffersByClient = initialsOfferRequest();
        CreditsV3Entity creditsV3Entity = creditsEntityWithAcceptOffer();
        OfferEntityV3 offerEntityV3 = createOfferEntityV3Valid();
        when(creditsV3Repository.save(creditsV3ArgumentCaptor.capture())).thenReturn(Try.of(() -> creditsV3Entity));
        when(createOffersService.calculate(offerInformationRequestCaptor.capture())).thenReturn(Collections.singletonList(offerEntityV3));
        Try<CreditsV3Entity> response = initialsOffersV3UseCaseTest.execute(getOffersByClient);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.get().getIdCredit(), is(creditsV3Entity.getIdCredit()));
        assertThat(creditsV3ArgumentCaptor.getValue().getInitialOffer().getTypeOffer(), is(OK.name()));
    }

    @Test
    public void GenerateOffersCO() {
        GetOffersByClient getOffersByClient = getGetOffersByClientRequest(30000000d);
        CreditsV3Entity creditsV3Entity = creditsEntityWithAcceptOffer();
        OfferEntityV3 offerEntityV3 = createOfferEntityV3Valid();
        when(creditsV3Repository.save(creditsV3ArgumentCaptor.capture())).thenReturn(Try.of(() -> creditsV3Entity));
        when(createOffersService.calculate(offerInformationRequestCaptor.capture())).thenReturn(Collections.singletonList(offerEntityV3));
        Try<CreditsV3Entity> response = initialsOffersV3UseCaseTest.execute(getOffersByClient);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.get().getIdCredit(), is(creditsV3Entity.getIdCredit()));
        assertThat(creditsV3ArgumentCaptor.getValue().getInitialOffer().getTypeOffer(), is(CO.name()));

    }


    @Test
    public void GenerateOffersKO() {

        GetOffersByClient getOffersByClient = getGetOffersByClientRequest(5000000d);
        CreditsV3Entity creditsV3Entity = creditsEntityWithAcceptOffer();
        when(creditsV3Repository.save(creditsV3ArgumentCaptor.capture())).thenReturn(Try.of(() -> creditsV3Entity));
        when(createOffersService.calculate(offerInformationRequestCaptor.capture())).thenReturn(Collections.EMPTY_LIST);
        Try<CreditsV3Entity> response = initialsOffersV3UseCaseTest.execute(getOffersByClient);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.get().getIdCredit(), is(creditsV3Entity.getIdCredit()));
        assertThat(creditsV3ArgumentCaptor.getValue().getInitialOffer().getTypeOffer(), is(KO.name()));


    }

    private GetOffersByClient getGetOffersByClientRequest(Double amount) {
        GetOffersByClient getOffersByClient = initialsOfferRequest();
        return GetOffersByClient.builder()
                .clientInformation(getOffersByClient.getClientInformation())
                .idClient(getOffersByClient.getIdClient())
                .loanPurpose(getOffersByClient.getLoanPurpose())
                .riskEngineAnalysis(getOffersByClient.getRiskEngineAnalysis())
                .clientLoanRequestedAmount(amount)
                .build();
    }
}
