package com.lulobank.credits.starter;

import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum;
import com.lulobank.credits.services.outboundadapters.model.LoanConditionsEntity;
import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.dto.RiskEngineAnalysisV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.models.response.GetLoanResponse;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetLoanDetailAdapterTest extends AbstractBaseIntegrationTest {

    private static final String ID_CREDIT = "6589444b-7437-4eb9-8407-3c718c8a1745";
    private static final String ID_LOAN_MAMBU = "MQMJ710";
    private static final Double ZERO_AMOUNT = 0d;
    private static final Double AMOUNT = 550d;


    private GetLoanResponse getLoanResponse;
    private io.vavr.collection.List<CreditsV3Entity> credits;

    @Value("classpath:mocks/getloanddetail/GetLoanDetail-Response.json")
    private Resource responseGetLoanDetail;
    @Value("classpath:mocks/getloanddetail/GetloanDetail-EmptyResponse.json")
    private Resource responseGetLoanDetailEmpty;

    @Override
    protected void init() {
        getLoanResponse = new GetLoanResponse();
        getLoanResponse.setId("JXDIEI");
        getLoanResponse.setProductTypeKey("8a8187256bd203cd016bd241fed011c6");
        //
        List<GetLoanResponse.PaymentPlanItem> paymentPlanItemApiList = new ArrayList<>();
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-09-01T12:00:00", 10000d, CbsLoanStateEnum.PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-10-01T12:00:00", 10001d, CbsLoanStateEnum.PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-12-01T12:00:00", 10003d, CbsLoanStateEnum.LATE.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-11-15T13:00:00", 10005d, CbsLoanStateEnum.PENDING.name()));
        //
        getLoanResponse.setPaymentPlanItemApiList(paymentPlanItemApiList);
        //

        CreditsV3Entity creditsEntity = new CreditsV3Entity();
        creditsEntity.setIdCredit(UUID.fromString(ID_CREDIT));
        creditsEntity.setIdClient("292020202020");
        creditsEntity.setIdLoanAccountMambu(ID_LOAN_MAMBU);
        List<LoanConditionsEntity> loanConditionsList = new ArrayList<>();
        InitialOfferV3 initialOffer = new InitialOfferV3(500000d, 3000000d, 0f, 2710000d);
        RiskEngineAnalysisV3 riskEngineAnalysis = new RiskEngineAnalysisV3();
        riskEngineAnalysis.setInterestRate(0f);
        initialOffer.setRiskEngineAnalysis(riskEngineAnalysis);
        creditsEntity.setInitialOffer(initialOffer);
        credits = io.vavr.collection.List.of(creditsEntity);

    }

    @Test
    public void should_return_ok_since_client_has_loan() throws Exception {
        getLoanResponse.setBalance(getBalance(AMOUNT));
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class))).thenReturn(getLoanResponse);
        when(creditsV3Repository.findByidClientAndIdLoanAccountMambuNotNull(any())).thenReturn(credits);


        mockMvc.perform(MockMvcRequestBuilders
                .get("/{idClient}/products", ID_CREDIT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.readFileToString(responseGetLoanDetail.getFile(), StandardCharsets.UTF_8)));
    }

    @Test
    public void should_return__empty_credits_list_since_loan_is_closed() throws Exception {

        getLoanResponse.setBalance(getBalance(ZERO_AMOUNT));
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class))).thenReturn(getLoanResponse);
        when(creditsV3Repository.findByidClientAndIdLoanAccountMambuNotNull(any())).thenReturn(credits);


        mockMvc.perform(MockMvcRequestBuilders
                .get("/{idClient}/products", ID_CREDIT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.readFileToString(responseGetLoanDetailEmpty.getFile(), StandardCharsets.UTF_8)));
    }

    @Test
    public void should_return_ok_since_client_has_no_loan() throws Exception {
        when(repository.findByidClient(any())).thenReturn(new ArrayList<>());
        when(creditsV3Repository.findByidClientAndIdLoanAccountMambuNotNull(any())).thenReturn(credits);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/{idClient}/products", ID_CREDIT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.readFileToString(responseGetLoanDetailEmpty.getFile(), StandardCharsets.UTF_8)));
    }

    private GetLoanResponse.PaymentPlanItem initPaymentPlanItem(String dateString, Double value, String state) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        GetLoanResponse.PaymentPlanItem paymentPlanItem = new GetLoanResponse.PaymentPlanItem();
        paymentPlanItem.setState(state);
        paymentPlanItem.setTotalDue(value);
        paymentPlanItem.setDueDate(date);
        return paymentPlanItem;
    }

    private GetLoanResponse.Balance getBalance(Double amount) {
        GetLoanResponse.Balance balance = new GetLoanResponse.Balance();
        balance.setAmount(amount);
        return balance;
    }
}
