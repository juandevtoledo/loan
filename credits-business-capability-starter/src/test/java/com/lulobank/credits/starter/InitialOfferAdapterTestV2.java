package com.lulobank.credits.starter;

import com.lulobank.credits.sdk.dto.initialofferv2.ClientInformation;
import com.lulobank.credits.sdk.dto.initialofferv2.DocumentId;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.sdk.dto.initialofferv2.Phone;
import com.lulobank.credits.sdk.dto.initialofferv2.RiskEngineAnalysis;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.InitialOffer;
import com.lulobank.credits.services.utils.MapperBuilder;
import com.lulobank.credits.v3.dto.ClientInformationV3;
import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.dto.RiskEngineAnalysisV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.response.SimulatedLoanResponse;
import io.vavr.control.Try;
import org.junit.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.lulobank.credits.services.features.initialoffer.OffersTypeEnum.COMFORTABLE_LOAN;
import static com.lulobank.credits.services.features.initialoffer.OffersTypeEnum.FAST_LOAN;
import static com.lulobank.credits.services.features.initialoffer.OffersTypeEnum.FLEXIBLE_LOAN;
import static com.lulobank.credits.services.features.initialoffer.RiskModelResponse.KO;
import static com.lulobank.credits.services.features.initialoffer.RiskModelResponse.OK;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InitialOfferAdapterTestV2 extends AbstractBaseIntegrationTest {

    private static final Double AMOUNT = 20000000d;
    private static final String PURPOSE = "VIAJES";
    private static final Double FEE_INSURANCE = 0.00312;
    private static final Integer INSTALLMENTS = 48;
    private static final Float RISK_INTEREST_RATE = 18f;
    private static final Double RISK_AMOUNT = 20000000d;
    private static final Double RISK_MAX_AMOUNT_INSTALLMENT = 2000000d;
    private static final Double CORE_BANKING_MAX_AMOUNT_INSTALLMENT = 250000d;
    public static final String URL_SERVICE = "/products/v2/loan/client/{idClient}/initial-offer";


    @Override
    protected void init() {
    }

    @Test
    public void save_initial_offers_OK() throws Exception {
        GetOfferToClient getOfferToClient = getOfferToClient(AMOUNT, PURPOSE);
        save_initial_offers(getOfferToClient);
    }

    @Test
    public void save_initial_offers_OK_purposeEmpty() throws Exception {
        GetOfferToClient getOfferToClient = getOfferToClient(AMOUNT, null);
        save_initial_offers(getOfferToClient);
    }

    public void save_initial_offers(GetOfferToClient getOfferToClient) throws Exception {
        CreditsV3Entity credits = createCreditsForTestOK(randomUUID().toString(), getOfferToClient);

        when(flexibilitySdk.simulateLoan(any())).thenReturn(buildSimulatedResponse(CORE_BANKING_MAX_AMOUNT_INSTALLMENT));
        when(functionBrave.decorateChecked(any(), any())).thenReturn((t)->buildSimulatedResponse(CORE_BANKING_MAX_AMOUNT_INSTALLMENT));
        when(creditsV3Repository.save(any())).thenReturn(Try.of(()->credits));

        String response = mockMvc.perform(MockMvcRequestBuilders
                .post(URL_SERVICE, ID_CLIENT)
                .with(getBearerToken())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(getOfferToClient)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(creditsV3Repository, times(1)).save(creditsV3EntityCaptor.capture());
        CreditsV3Entity creditsEntityRequest = creditsV3EntityCaptor.getValue();

        assertEquals("IdClient is Right->", ID_CLIENT, creditsEntityRequest.getIdClient());
        assertTrue("InitialOffers is not empty->", Objects.nonNull(creditsEntityRequest.getInitialOffer()));
        assertEquals("InitialOffers type is OK->", OK.name(), creditsEntityRequest.getInitialOffer().getTypeOffer());
        assertEquals("InitialOffers  offerEntities contains 3->", 3, creditsEntityRequest.getInitialOffer().getOfferEntities().size());
        assertEquals("InitialOffers  offerEntities contains COMFORTABLE_LOAN->", 1,
                creditsEntityRequest.getInitialOffer().getOfferEntities().stream().filter(x -> x.getType().equals(COMFORTABLE_LOAN.name())).count());
        assertEquals("InitialOffers  offerEntities contains FAST_LOAN->", 1,
                creditsEntityRequest.getInitialOffer().getOfferEntities().stream().filter(x -> x.getType().equals(FAST_LOAN.name())).count());
        assertEquals("InitialOffers  offerEntities contains FLEXIBLE_LOAN->", 1,
                creditsEntityRequest.getInitialOffer().getOfferEntities().stream().filter(x -> x.getType().equals(FLEXIBLE_LOAN.name())).count());
    }

    @Test
    public void save_initial_offers_KO() throws Exception {

        GetOfferToClient getOfferToClient = getOfferToClient(AMOUNT, PURPOSE);
        getOfferToClient.getRiskEngineAnalysis().setMaxAmountInstallment(10000d);
        CreditsV3Entity credits = createCreditsForTestOK(randomUUID().toString(), getOfferToClient);

        when(flexibilitySdk.simulateLoan(any())).thenReturn(buildSimulatedResponse(CORE_BANKING_MAX_AMOUNT_INSTALLMENT));
        when(functionBrave.decorateChecked(any(), any())).thenReturn((t)->buildSimulatedResponse(CORE_BANKING_MAX_AMOUNT_INSTALLMENT));
        when(creditsV3Repository.save(any())).thenReturn(Try.of(()->credits));

        String response = mockMvc.perform(MockMvcRequestBuilders
                .post(URL_SERVICE, ID_CLIENT)
                .with(getBearerToken())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(getOfferToClient)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(creditsV3Repository, times(1)).save(creditsV3EntityCaptor.capture());
        CreditsV3Entity creditsEntityRequest = creditsV3EntityCaptor.getValue();
        assertEquals("IdClient is Right->", ID_CLIENT, creditsEntityRequest.getIdClient());
        assertTrue("InitialOffers is not empty->", Objects.nonNull(creditsEntityRequest.getInitialOffer()));
        assertEquals("InitialOffers type is OK->", KO.name(), creditsEntityRequest.getInitialOffer().getTypeOffer());
    }

    @Test
    public void save_initial_offers_KO_since_flexibility_throws_exception() throws Exception {

        GetOfferToClient getOfferToClient = getOfferToClient(AMOUNT, PURPOSE);
        getOfferToClient.getRiskEngineAnalysis().setMaxAmountInstallment(10000d);
        CreditsV3Entity credits = createCreditsForTestOK(randomUUID().toString(), getOfferToClient);

        when(functionBrave.decorateChecked(any(), any())).thenReturn((t)-> {
            throw  new ProviderException("Error", "502");
        });
        when(creditsV3Repository.save(any())).thenReturn(Try.of(()->credits));

        String response = mockMvc.perform(MockMvcRequestBuilders
                .post(URL_SERVICE, ID_CLIENT)
                .with(getBearerToken())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(getOfferToClient)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(creditsV3Repository, times(1)).save(creditsV3EntityCaptor.capture());
        CreditsV3Entity creditsEntityRequest = creditsV3EntityCaptor.getValue();
        assertEquals("IdClient is Right->", ID_CLIENT, creditsEntityRequest.getIdClient());
        assertTrue("InitialOffers is not empty->", Objects.nonNull(creditsEntityRequest.getInitialOffer()));
        assertEquals("InitialOffers type is OK->", KO.name(), creditsEntityRequest.getInitialOffer().getTypeOffer());
    }

    @Test
    public void return_initial_offers_CO_with_Comfortable_Loan() throws Exception {

        GetOfferToClient getOfferToClient = getOfferToClient(AMOUNT, PURPOSE);
        CreditsV3Entity credits = createCreditsForTestOK(randomUUID().toString(), getOfferToClient);
        when(creditsV3Repository.save(any())).thenReturn(Try.of(()->credits));
        when(flexibilitySdk.simulateLoan(Mockito.argThat(new IsSimulateRequest(FAST_LOAN.getInstallment())))).thenReturn(buildSimulatedResponse(RISK_MAX_AMOUNT_INSTALLMENT + 1));
        when(flexibilitySdk.simulateLoan(Mockito.argThat(new IsSimulateRequest(COMFORTABLE_LOAN.getInstallment())))).thenReturn(buildSimulatedResponse(CORE_BANKING_MAX_AMOUNT_INSTALLMENT));
        when(functionBrave.decorateChecked(any(), any())).thenReturn((t)->buildSimulatedResponse(CORE_BANKING_MAX_AMOUNT_INSTALLMENT));

        String response = mockMvc.perform(MockMvcRequestBuilders
                .post(URL_SERVICE, ID_CLIENT)
                .with(getBearerToken())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(getOfferToClient)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(creditsV3Repository, times(1)).save(creditsV3EntityCaptor.capture());

    }

    @Test
    public void initial_offers_is_bad_request() throws Exception {

        GetOfferToClient getOfferToClient = getOfferToClient(null, PURPOSE);

        String response = mockMvc.perform(MockMvcRequestBuilders
                .post(URL_SERVICE, ID_CLIENT)
                .with(getBearerToken())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(getOfferToClient)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void should_return_forbidden() throws Exception {
        GetOfferToClient getOfferToClient = getOfferToClient(AMOUNT, PURPOSE);
        mockMvc.perform(MockMvcRequestBuilders
                .post(URL_SERVICE, UUID.randomUUID())
                .with(getBearerToken())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(getOfferToClient)))
                .andExpect(status().isForbidden())
                .andReturn();
    }


    private GetOfferToClient getOfferToClient(Double amount, String purpose) {
        GetOfferToClient getOfferToClient = new GetOfferToClient();
        getOfferToClient.setClientLoanRequestedAmount(amount);
        getOfferToClient.setLoanPurpose(purpose);
        ClientInformation clientInformation = new ClientInformation();
        DocumentId documentId = new DocumentId();
        documentId.setId("111111");
        documentId.setExpirationDate("1999-01-01");
        documentId.setType("CC");
        clientInformation.setDocumentId(documentId);
        clientInformation.setEmail("prueba.juantoledo@gmail.com");
        clientInformation.setGender("M");
        clientInformation.setLastName("TEST");
        clientInformation.setName("TES");
        Phone phone = new Phone();
        phone.setNumber("3135859595");
        phone.setPrefix("57");
        clientInformation.setPhone(phone);
        getOfferToClient.setClientInformation(clientInformation);
        RiskEngineAnalysis riskEngineAnalysis = new RiskEngineAnalysis();
        riskEngineAnalysis.setAmount(RISK_AMOUNT);
        riskEngineAnalysis.setInterestRate(RISK_INTEREST_RATE);
        riskEngineAnalysis.setMaxAmountInstallment(RISK_MAX_AMOUNT_INSTALLMENT);
        getOfferToClient.setRiskEngineAnalysis(riskEngineAnalysis);
        return getOfferToClient;

    }

    private SimulatedLoanResponse buildSimulatedResponse(Double maxAmountInstallCoreBanking) {

        SimulatedLoanResponse simulatedLoanResponse = new SimulatedLoanResponse();
        List<SimulatedLoanResponse.Repayment> repayments = new ArrayList<>();
        repayments.add(createRepayment(maxAmountInstallCoreBanking));
        repayments.add(createRepayment(maxAmountInstallCoreBanking - 10d));
        repayments.add(createRepayment(maxAmountInstallCoreBanking - 9d));
        simulatedLoanResponse.setRepayment(repayments);
        return simulatedLoanResponse;
    }

    private SimulatedLoanResponse.Repayment createRepayment(Double totalDue) {
        SimulatedLoanResponse.Repayment repayment = new SimulatedLoanResponse.Repayment();
        repayment.setTotalDue(totalDue);
        return repayment;
    }

    private CreditsV3Entity createCreditsForTestOK(String idCredit, GetOfferToClient getOfferToClient) {
        CreditsV3Entity creditsEntity = new CreditsV3Entity();
        creditsEntity.setIdCredit(UUID.fromString(idCredit));
        creditsEntity.setClientInformation(clientInformationFromRequest(getOfferToClient));
        InitialOfferV3 initialOffer = new InitialOfferV3();
        initialOffer.setRiskEngineAnalysis(riskEngineAnalysisFromRequest(getOfferToClient));
        initialOffer.setTypeOffer(OK.name());
        creditsEntity.setInitialOffer(initialOffer);
        return creditsEntity;
    }

    public static ClientInformationV3 clientInformationFromRequest(GetOfferToClient getOfferToClient) {
        return new ModelMapper().map(getOfferToClient.getClientInformation(), ClientInformationV3.class);
    }

    public static RiskEngineAnalysisV3 riskEngineAnalysisFromRequest(GetOfferToClient getOfferToClient) {
        return new ModelMapper().map(getOfferToClient.getRiskEngineAnalysis(), RiskEngineAnalysisV3.class);
    }

    private CreditsEntity createCreditsForTestko(String idCredit, GetOfferToClient getOfferToClient) {
        CreditsEntity creditsEntity = new CreditsEntity();
        creditsEntity.setIdCredit(UUID.fromString(idCredit));
        creditsEntity.setClientInformation(MapperBuilder.clientInformationFromRequest(getOfferToClient));
        InitialOffer initialOffer = new InitialOffer();
        initialOffer.setRiskEngineAnalysis(MapperBuilder.riskEngineAnalysisFromRequest(getOfferToClient));
        initialOffer.setTypeOffer(KO.name());
        return creditsEntity;
    }
}
