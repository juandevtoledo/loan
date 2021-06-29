package com.lulobank.credits.starter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lulobank.credits.sdk.dto.CreditSuccessResult;
import com.lulobank.credits.sdk.dto.clientloandetail.Installment;
import com.lulobank.credits.sdk.dto.clientloandetail.Loan;
import com.lulobank.credits.sdk.dto.clientloandetail.LoanDetail;
import com.lulobank.credits.sdk.dto.clientloandetail.Payment;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.PaymentType;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import com.lulobank.credits.starter.utils.Constants;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetLoanMovementsRequest;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.models.response.GetLoanMovementsResponse;
import flexibility.client.models.response.GetLoanResponse;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityInstallmentPendingStateEnum.PENDING;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityLoanMovementsPaidStatesEnum.PAID;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClientLoanAdapterTest extends AbstractBaseIntegrationTest {

    public static final Double AMOUNT = 1000000d;
    private GetLoanResponse getLoanResponse;
    private GetLoanMovementsResponse loanMovementsResponse;
    private List<CreditsEntity> credits;
    private CreditsEntity creditsEntity;
    private static final String ID_CREDIT = "6589444b-7437-4eb9-8407-3c718c8a1745";
    private static final String ID_LOAN_MAMBU = "MQMJ710";
    private static final String ID_CLIENT_MAMBU = "12828292";
    private static final Double INSTALLMENT_AMOUNT = 10000d;
    private static final Integer INSTALLMENT = 12;
    private static final Double CREDIT_AMOUNT = 100000.0d;
    private static final String CREATION_DATE_LOAN = "2020-01-01T10:00:00";
    private static final Float INTEREST_RATE = 16.5f;
    private static final Float MONTHLY_NOMINAL_RATE = 1.281f;

    @Value("classpath:mocks/getclientloan/GetClientLoan-Response.json")
    private Resource responseGetClientLoan;
    @Value("classpath:mocks/getclientloan/GetClientLoan-empty-Response.json")
    private Resource responseGetClientLoanEmpty;
    @Value("classpath:mocks/getclientloan/GetClientLoan-LoanDetail-Response.json")
    private Resource responseGetClientLoanLoanDetail;
    @Value("classpath:mocks/getclientloan/GetClientLoan-BadRequest-Response.json")
    private Resource responseGetClientLoanBadRequest;
    @Value("classpath:mocks/getclientloan/GetClientLoan-Closed-Response.json")
    private Resource responseGetClientLoanClosed;

    @Override
    protected void init() {

        getLoanResponse = new GetLoanResponse();
        getLoanResponse.setId(ID_LOAN_MAMBU);
        getLoanResponse.setProductTypeKey("8a8187256bd203cd016bd241fed011c6");
        getLoanResponse.setAccountState("ACTIVE");
        getLoanResponse.setCreationDate(LocalDateTime.parse(CREATION_DATE_LOAN));
        GetLoanResponse.LoanAmount loanAmountInstallment = new GetLoanResponse.LoanAmount();
        loanAmountInstallment.setAmount(INSTALLMENT_AMOUNT);
        getLoanResponse.setInstallmentTotalDue(loanAmountInstallment);

        List<GetLoanResponse.PaymentPlanItem> paymentPlanItemApiList = new ArrayList<>();
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-09-01T12:00:00", INSTALLMENT_AMOUNT, PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-10-01T12:00:00", INSTALLMENT_AMOUNT, PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-12-01T12:00:00", INSTALLMENT_AMOUNT, PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-11-15T13:00:00", INSTALLMENT_AMOUNT, PENDING.name()));

        GetLoanResponse.Balance balance = new GetLoanResponse.Balance();
        balance.setAmount(getBalance(Long.valueOf(paymentPlanItemApiList.stream().filter(x -> x.getState().equals(PAID.name())).count()).intValue()));
        getLoanResponse.setBalance(balance);
        GetLoanResponse.Balance totalBalance = new GetLoanResponse.Balance();
        totalBalance.setAmount(balance.getAmount()+283.45);
        getLoanResponse.setTotalBalance(totalBalance);

        GetLoanResponse.LoanAmount loanAmount = new GetLoanResponse.LoanAmount();
        loanAmount.setAmount(CREDIT_AMOUNT);
        getLoanResponse.setLoanAmount(loanAmount);
        getLoanResponse.setInterestRate(INTEREST_RATE);


        getLoanResponse.setPaymentPlanItemApiList(paymentPlanItemApiList);

        credits = new ArrayList<>();
        creditsEntity = new CreditsEntity();
        creditsEntity.setIdCredit(UUID.fromString(ID_CREDIT));
        creditsEntity.setIdClient(Constants.ID_CLIENT);
        creditsEntity.setIdClientMambu(ID_CLIENT_MAMBU);
        creditsEntity.setIdLoanAccountMambu(ID_LOAN_MAMBU);
        creditsEntity.setAutomaticDebit(false);
        creditsEntity.setAcceptDate(LocalDateTime.parse("2020-09-04T14:28:50"));
        OfferEntity offerEntity = new OfferEntity();
        offerEntity.setAmount(CREDIT_AMOUNT);
        offerEntity.setAmountInstallment(INSTALLMENT_AMOUNT);
        offerEntity.setInstallments(INSTALLMENT);
        offerEntity.setInterestRate(INTEREST_RATE);
        offerEntity.setMonthlyNominalRate(BigDecimal.valueOf(MONTHLY_NOMINAL_RATE));
        creditsEntity.setAcceptOffer(offerEntity);
        credits.add(creditsEntity);

        loanMovementsResponse = new GetLoanMovementsResponse();
        List<GetLoanMovementsResponse.LoanMovement> loanMovements = new ArrayList<>();
        GetLoanMovementsResponse.LoanMovement loanMovement = new GetLoanMovementsResponse.LoanMovement();
        loanMovement.setAmount(AMOUNT);
        GetLoanMovementsResponse.Amounts amounts = new GetLoanMovementsResponse.Amounts();
        amounts.setFeesAmount(322.52);
        amounts.setPrincipalAmount(935750.6);
        amounts.setInterestAmount(63926.88);
        loanMovement.setAmounts(amounts);
        loanMovement.setTransactionType(PaymentType.LOAN_REPAYMENT.name());
        loanMovements.add(loanMovement);
        loanMovement.setCreationDate(LocalDateTime.of(2019, 12, 05, 20, 20, 20));
        loanMovementsResponse.setLoanMovementList(loanMovements);
        when(creditsConditionDomain.getMinimumPayDay()).thenReturn(15);

    }

    @Test
    public void get_client_loan_ok() throws Exception {
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class))).thenReturn(getLoanResponse);
        when(repository.findByidClientAndLoanStatusIsNotAndAcceptDateNotNull(any(), any())).thenReturn(credits);
        when(repository.findByIdCredit(any())).thenReturn(Optional.ofNullable(creditsEntity));
        when(flexibilitySdk.getLoanMovements(any(GetLoanMovementsRequest.class))).thenReturn(loanMovementsResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/loan/client/{idClient}", Constants.ID_CLIENT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.readFileToString(responseGetClientLoan.getFile(), StandardCharsets.UTF_8)));
        Mockito.verify(flexibilitySdk, times(1)).getLoanByLoanAccountId(getLoanRequestCaptor.capture());
        Mockito.verify(flexibilitySdk, times(1)).getLoanMovements(getLoanMovementsRequestCaptor.capture());
    }

    @Test
    public void get_client_loan_empty_since_credits_idClientMambu_is_null() throws Exception {
        List<CreditsEntity> creditsEntitiesEmpty = new ArrayList<>();
        when(repository.findByidClientAndLoanStatusIsNotAndAcceptDateNotNull(any(), any())).thenReturn(creditsEntitiesEmpty);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/loan/client/{idClient}", Constants.ID_CLIENT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.readFileToString(responseGetClientLoanEmpty.getFile(), StandardCharsets.UTF_8)));

    }

    @Test
    public void get_client_loan_Detail_since_getLoanResponse_is_null() throws Exception {
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class))).thenReturn(null);
        when(repository.findByidClientAndLoanStatusIsNotAndAcceptDateNotNull(any(), any())).thenReturn(credits);
        when(repository.findByIdCredit(any())).thenReturn(Optional.ofNullable(creditsEntity));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/loan/client/{idClient}", Constants.ID_CLIENT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.readFileToString(responseGetClientLoanEmpty.getFile(), StandardCharsets.UTF_8)));

    }

    @Test
    public void get_client_loan_Detail_since_core_banking_throws_exception() throws Exception {

        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class))).thenThrow(new ProviderException("PE", "PE"));
        when(repository.findByidClientAndLoanStatusIsNotAndAcceptDateNotNull(any(), any())).thenReturn(credits);
        when(repository.findByIdCredit(any())).thenReturn(Optional.ofNullable(creditsEntity));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/loan/client/{idClient}", Constants.ID_CLIENT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.readFileToString(responseGetClientLoanEmpty.getFile(), StandardCharsets.UTF_8)));

    }

    @Test
    public void get_client_loan_return_bad_request() throws Exception {
        String idClientBad = "i";
        mockMvc.perform(MockMvcRequestBuilders
                .get("/loan/client/{idClient}", idClientBad)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void update_credit_entity_with_loan_status_is_closed() throws Exception {
        getLoanResponse.setAccountState(CbsLoanStateEnum.CLOSED.name());
        getLoanResponse.setLastModifiedDate(closedDate());
        when(repository.findByIdCredit(any())).thenReturn(Optional.ofNullable(creditsEntity));
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class))).thenReturn(getLoanResponse);
        when(repository.findByidClientAndLoanStatusIsNotAndAcceptDateNotNull(any(), any())).thenReturn(credits);
        when(flexibilitySdk.getLoanMovements(any(GetLoanMovementsRequest.class))).thenReturn(loanMovementsResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/loan/client/{idClient}", Constants.ID_CLIENT)
                .with(getBearerToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.readFileToString(responseGetClientLoanClosed.getFile(), StandardCharsets.UTF_8)));

        Mockito.verify(flexibilitySdk, times(1)).getLoanByLoanAccountId(getLoanRequestCaptor.capture());
        Mockito.verify(flexibilitySdk, times(1)).getLoanMovements(getLoanMovementsRequestCaptor.capture());
        Mockito.verify(repository, times(1)).save(creditsEntityCaptor.capture());
    }

    private LocalDateTime closedDate() {
        String closeDate = "2016-03-04 11:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(closeDate, formatter);
    }

    private GetLoanResponse.PaymentPlanItem initPaymentPlanItem(String dateString, Double value, String state) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        GetLoanResponse.PaymentPlanItem paymentPlanItem = new GetLoanResponse.PaymentPlanItem();
        paymentPlanItem.setState(state);
        paymentPlanItem.setTotalDue(value);
        paymentPlanItem.setDueDate(date);
        return paymentPlanItem;
    }

    private double getBalance(Integer installmentsPaid) {
        return CREDIT_AMOUNT - (INSTALLMENT_AMOUNT * installmentsPaid);
    }

}
