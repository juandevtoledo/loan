package com.lulobank.credits.starter;

import brave.SpanCustomizer;
import brave.http.HttpTracing;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.core.events.Event;
import com.lulobank.credits.services.domain.CreditsConditionDomain;
import com.lulobank.credits.services.features.services.PendingValidationsService;
import com.lulobank.credits.services.features.services.SQSMessageService;
import com.lulobank.credits.services.inboundadapters.GetLoanDetailAdapter;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.repository.CreditsRepository;
import com.lulobank.credits.services.outboundadapters.riskengine.IRiskEngineOperation;
import com.lulobank.credits.services.port.inbound.OtpService;
import com.lulobank.credits.starter.config.ResilienceConfig;
import com.lulobank.credits.starter.inboundadapter.CreditsInboundAdapter;
import com.lulobank.credits.starter.utils.BearerTokenRequestPostProcessor;
import com.lulobank.credits.v3.port.in.clientinformation.UpdateProductEmailUseCase;
import com.lulobank.credits.v3.port.in.digitalevidence.DigitalEvidenceCreatedUseCase;
import com.lulobank.credits.v3.port.in.promissorynote.PromissoryNoteCreatedUseCase;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.usecase.acceptoffer.AcceptOfferUseCase;
import com.lulobank.promissorynote.sdk.operations.IPromissoryNote;
import com.lulobank.savingsaccounts.sdk.operations.ISavingsAccount;
import com.lulobank.tracing.BraveTracerWrapper;
import com.lulobank.tracing.FunctionBrave;
import flexibility.client.models.request.AddSettlementAccountForLoanRequest;
import flexibility.client.models.request.CreateClientRequest;
import flexibility.client.models.request.CreateLoanRequest;
import flexibility.client.models.request.DeleteSettlementAccountForLoanRequest;
import flexibility.client.models.request.GetLoanMovementsRequest;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.models.request.PaymentRequest;
import flexibility.client.models.request.SimulatedLoanRequest;
import flexibility.client.sdk.FlexibilitySdk;
import okhttp3.OkHttpClient;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import retrofit2.Retrofit;

import java.util.Map;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@WebMvcTest({GetLoanDetailAdapter.class, CreditsInboundAdapter.class})
@Import({ResilienceConfig.class})
@ActiveProfiles(profiles = "test")
public abstract class AbstractBaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
    @MockBean
    protected CreditsRepository repository;
    @MockBean
    protected IRiskEngineOperation riskEngineOperation;
    @MockBean
    protected CreditsConditionDomain creditsConditionDomain;
    @MockBean
    protected FlexibilitySdk flexibilitySdk;
    @MockBean
    protected RetrofitClientOperations retrofitClientOperations;
    @MockBean
    protected PendingValidationsService pendingValidationsService;
    @MockBean
    protected SQSMessageService sqsMessageService;
    @MockBean
    protected SimpleMessageListenerContainer simpleMessageListenerContainer;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected UpdateProductEmailUseCase updateProductEmailUseCase;
    @MockBean
    protected DigitalEvidenceCreatedUseCase digitalEvidenceCreatedUseCase;
    @MockBean
    protected PromissoryNoteCreatedUseCase promissoryNoteCreatedUseCase;
    @MockBean
    protected ISavingsAccount savingsAccountService;
    @MockBean
    protected IPromissoryNote promissoryNoteService;
    @MockBean(name="okHttpBuilderTracing")
    protected OkHttpClient.Builder builder;
    @MockBean
    protected BraveTracerWrapper braveTracerWrapper;
    @MockBean
    protected OtpService getOtpService;
    @MockBean(name="promissory")
    protected Retrofit retrofitPromissory;
    @MockBean(name="risk")
    protected Retrofit retrofit;
    @MockBean(name="retrofitSavings")
    protected Retrofit retrofitSavings;
    @MockBean(name="retrofitOtp")
    protected Retrofit retrofitOtp;
    @MockBean(name="retrofitOtpClient")
    protected Retrofit retrofitOtpClient;
    @MockBean
    protected HttpTracing tracing;
    @MockBean(name = "savingsRestTemplate")
    protected RestTemplateClient savingsRestTemplate;
    @MockBean(name = "clientsRestTemplate")
    protected RestTemplateClient clientsRestTemplate;
    @MockBean
    protected RestTemplateBuilder restTemplateBuilder;
    @MockBean
    protected CreditsV3Repository creditsV3Repository;
    @MockBean
    protected FunctionBrave functionBrave;
    @MockBean
    protected SpanCustomizer spanCustomizer;
    @MockBean
    protected AcceptOfferUseCase acceptOfferUseCase;
    @Captor
    protected ArgumentCaptor<CreditsEntity> creditsEntityCaptor;
    @Captor
    protected ArgumentCaptor<CreditsV3Entity> creditsV3EntityCaptor;
    @Captor
    protected ArgumentCaptor<CreateLoanRequest> createLoanRequestCaptor;
    @Captor
    protected ArgumentCaptor<CreateClientRequest> createClientRequestCaptor;
    @Captor
    protected ArgumentCaptor<SimulatedLoanRequest> simulatedLoanRequestArgumentCaptor;
    @Captor
    protected ArgumentCaptor<Event> eventArgumentCaptor;
    @Captor
    protected ArgumentCaptor<DeleteSettlementAccountForLoanRequest> deleteSettlementAccountForLoanRequestCaptor;
    @Captor
    protected ArgumentCaptor<AddSettlementAccountForLoanRequest> addSettlementAccountForLoanRequestCaptor;
    @Captor
    protected ArgumentCaptor<GetLoanRequest> getLoanRequestCaptor;
    @Captor
    protected ArgumentCaptor<GetLoanMovementsRequest> getLoanMovementsRequestCaptor;
    @Captor
    protected ArgumentCaptor<PaymentRequest> paymentRequestCaptor;
    @Captor
    protected ArgumentCaptor<Map<String, String>> headersCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        init();
    }

    protected static BearerTokenRequestPostProcessor getBearerToken() {
        return new BearerTokenRequestPostProcessor();
    }

    protected abstract void init();
}
