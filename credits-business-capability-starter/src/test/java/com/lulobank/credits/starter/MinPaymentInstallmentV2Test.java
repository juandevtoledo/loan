package com.lulobank.credits.starter;

import com.lulobank.core.events.Event;
import com.lulobank.credits.sdk.dto.payment.PaymentInstallment;
import com.lulobank.credits.sdk.dto.payment.ReduceInstallment;
import com.lulobank.credits.services.events.GoodStandingCertificateEvent;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum;
import com.lulobank.credits.services.utils.SavingAccountState;
import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.dto.LoanConditionsEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetAccountRequest;
import flexibility.client.models.request.PaymentRequest;
import flexibility.client.models.response.GetAccountResponse;
import flexibility.client.models.response.PaymentResponse;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MinPaymentInstallmentV2Test extends AbstractBaseIntegrationTest {

    private static final String TESTED_URL = "/loan/client/{idClient}/payment/installment";
    private static final String ID_CREDIT = "031769d9-a10b-40c1-a349-23ff42a4779b";
    private static final String ID_CREDIT_CBS = "JERB107";
    private static final String ID_CLIENT_CBANKING = "433498577";
    private static final String CONTENT_TYPE = "application/json";
    private static final String PAYMENT_STATUS = "ACCEPT";
    private static final String SAVING_ACCOUNT = "ABC123";
    private PaymentResponse paymentResponse;
    private CreditsV3Entity creditsEntity;
    private List<GetAccountResponse> accounts;
    private PaymentInstallment paymentInstallment;
    @Value("classpath:mocks/paymentinstallment/PaymentCredit-CreditNotExist-Response.json")
    private Resource responsePaymentCreditNotExist;
    @Value("classpath:mocks/paymentinstallment/PaymentCredit-IdCreditNull-Response.json")
    private Resource responsePaymentIdcreditNull;
    @Value("classpath:mocks/paymentinstallment/PaymentCredit-FlexibilityError-Response.json")
    private Resource responsePaymentFlexilibilityError;
    @Value("classpath:mocks/paymentinstallment/requestPaymentInstallmentWithPaidInFull.json")
    private Resource requestPaymentInstallmentWithPaidInFull;
    @Value("classpath:mocks/paymentinstallment/requestPaymentInstallmentCreditNull.json")
    private Resource requestPaymentInstallmentCreditNull;
    @Value("classpath:mocks/paymentinstallment/PaymentCredit-WithdrawalPastOverdraftConstraints-Response.json")
    private Resource requestPaymentInstallmentWithdrawalPast;
    @Value("classpath:mocks/paymentinstallment/requestPaymentInstallmentWithNotPaidInFull.json")
    private Resource requestPaymentInstallmentWithNotPaidInFull;

    @Override
    protected void init() {
        creditsEntity = new CreditsV3Entity();
        creditsEntity.setIdCredit(UUID.fromString(ID_CREDIT));
        creditsEntity.setIdClientMambu(ID_CLIENT_CBANKING);
        creditsEntity.setIdLoanAccountMambu(ID_CREDIT_CBS);
        creditsEntity.setIdSavingAccount(SAVING_ACCOUNT);
        creditsEntity.setIdClient(ID_CLIENT);
        creditsEntity.setAcceptDate(LocalDateTime.now());
        List<LoanConditionsEntityV3> loanConditionsList = new ArrayList<>();
        InitialOfferV3 initialOffer = new InitialOfferV3(500000d, 3000000d, 0f, 2710000d);
        creditsEntity.setInitialOffer(initialOffer);
        creditsEntity.setLoanConditionsList(loanConditionsList);
        creditsEntity.setDayOfPay(5);
        creditsEntity.setStatementsIndex("5#APPROVED");
        paymentResponse = new PaymentResponse();
        paymentResponse.setStatus(PAYMENT_STATUS);
        accounts = new ArrayList<>();
        GetAccountResponse accountResponse = new GetAccountResponse();
        accountResponse.setNumber(SAVING_ACCOUNT);
        accountResponse.setState(SavingAccountState.ACTIVE.name());
        GetAccountResponse.Balance balance = new GetAccountResponse.Balance();
        balance.setAmount(10000.0);
        accountResponse.setBalance(balance);
        accounts.add(accountResponse);

        paymentInstallment = new PaymentInstallment();
        paymentInstallment.setAmount(Double.valueOf("2000"));
        paymentInstallment.setIdCredit(ID_CREDIT);
        paymentInstallment.setIdCreditCBS(ID_CREDIT_CBS);
        paymentInstallment.setPaidInFull(false);
        paymentInstallment.setReduce(ReduceInstallment.NONE);
    }

    @Test
    public void ShouldReturnAcceptedPaymentInstallmentWithPaidFull() throws Exception {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(String.class), any(String.class))).thenReturn(Option.of(creditsEntity));
        when(flexibilitySdk.paymentLoan(any(PaymentRequest.class))).thenReturn(paymentResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .post(TESTED_URL, ID_CLIENT)
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(FileUtils.readFileToString(requestPaymentInstallmentWithPaidInFull.getFile(), StandardCharsets.UTF_8)))
                .andExpect(status().isAccepted());
        Mockito.verify(flexibilitySdk, times(1)).paymentLoan(paymentRequestCaptor.capture());
        Mockito.verify(creditsV3Repository, times(1)).findByIdCreditAndIdLoanAccountMambu(any(), any());
        Mockito.verify(creditsV3Repository, times(1)).save(creditsV3EntityCaptor.capture());
        Mockito.verify(sqsMessageService, times(1)).sendMessageReportingQueue(eventArgumentCaptor.capture(), any());
        Event<GoodStandingCertificateEvent> evenGoodStanding = eventArgumentCaptor.getValue();
        CreditsV3Entity creditsEntitySave = creditsV3EntityCaptor.getValue();
        PaymentRequest paymentRequest = paymentRequestCaptor.getValue();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        assertEquals("Payment request id Client is right", ID_CLIENT_CBANKING, paymentRequest.getClientId());
        assertEquals("Payment request id loan is right", ID_CREDIT_CBS, paymentRequest.getLoanAccountId());
        assertEquals("Payment request amount is right", Double.valueOf(200000), paymentRequest.getAmount().getAmount());
        assertEquals("Payment request id saving account is right", SAVING_ACCOUNT, paymentRequest.getAccount().getNumber());
        assertTrue("Credit Closed Date is not null", Objects.nonNull(creditsEntitySave.getClosedDate()));
        assertEquals("Credit Loan status is Closed", CbsLoanStateEnum.CLOSED.name(), creditsEntitySave.getLoanStatus().getStatus());
        assertTrue("Credit Sent Certified is true", creditsEntitySave.getLoanStatus().getCertificationSent());
        assertEquals("Event Good standing idClient is Right", ID_CLIENT, evenGoodStanding.getPayload().getIdClient());
        assertEquals("Event Good standing id Cbs is Right", ID_CREDIT_CBS, evenGoodStanding.getPayload().getIdLoanAccountMambu());
        assertEquals("Event Good standing Close Date is Right", creditsEntitySave.getClosedDate().format(dtf),
                evenGoodStanding.getPayload().getClosedDate());
        assertEquals("Event Good standing Accept Date is Right", creditsEntity.getAcceptDate().format(dtf), evenGoodStanding.getPayload().getAcceptDate());
        assertThat(paymentRequest.getPayOff(), is(true));
        assertThat(creditsEntitySave.getStatementsIndex(), is("5#CLOSED"));
    }

    @Test
    public void ShouldReturnAcceptedPaymentInstallmentWithNotPaidFull() throws Exception {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(String.class), any(String.class))).thenReturn(Option.of(creditsEntity));
        when(flexibilitySdk.paymentLoan(any(PaymentRequest.class))).thenReturn(paymentResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .post(TESTED_URL, ID_CLIENT)
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(FileUtils.readFileToString(requestPaymentInstallmentWithNotPaidInFull.getFile(), StandardCharsets.UTF_8)))
                .andExpect(status().isAccepted());
        Mockito.verify(flexibilitySdk, times(1)).paymentLoan(paymentRequestCaptor.capture());
        Mockito.verify(creditsV3Repository, times(1)).findByIdCreditAndIdLoanAccountMambu(any(), any());
        Mockito.verify(creditsV3Repository, times(1)).save(creditsV3EntityCaptor.capture());
        CreditsV3Entity creditsEntitySave = creditsV3EntityCaptor.getValue();
        PaymentRequest paymentRequest = paymentRequestCaptor.getValue();
        assertEquals("Payment request id Client is right", ID_CLIENT_CBANKING, paymentRequest.getClientId());
        assertEquals("Payment request id loan is right", ID_CREDIT_CBS, paymentRequest.getLoanAccountId());
        assertEquals("Payment request amount is right", Double.valueOf(200000), paymentRequest.getAmount().getAmount());
        assertEquals("Payment request id saving account is right", SAVING_ACCOUNT, paymentRequest.getAccount().getNumber());
        assertTrue("Credit Closed Date is  null", Objects.isNull(creditsEntitySave.getClosedDate()));
        assertTrue("Credit Loan status is null", Objects.isNull(creditsEntitySave.getLoanStatus()));

    }

    @Test
    public void ShouldReturnInternalErrorPaymentInstallmentCreditNotExist() throws Exception {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(String.class), any(String.class))).thenReturn(Option.none());
        when(flexibilitySdk.paymentLoan(any(PaymentRequest.class))).thenReturn(paymentResponse);
        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(FileUtils.readFileToString(requestPaymentInstallmentWithPaidInFull.getFile(), StandardCharsets.UTF_8)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(FileUtils.readFileToString(responsePaymentCreditNotExist.getFile(), StandardCharsets.UTF_8)));
    }

    @Test
    public void ShouldReturnInternalErrorPaymentInstallmentIdCreditNull() throws Exception {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(String.class), any(String.class))).thenReturn(Option.of(creditsEntity));

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(FileUtils.readFileToString(requestPaymentInstallmentCreditNull.getFile(), StandardCharsets.UTF_8)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void ShouldReturnInternalErrorPaymentInstallmentFlexibilityError() throws Exception {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(String.class), any(String.class))).thenReturn(Option.of(creditsEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(flexibilitySdk.paymentLoan(any(PaymentRequest.class))).thenThrow(new ProviderException("FLEXIBILITY_ERROR", "500"));

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(FileUtils.readFileToString(requestPaymentInstallmentWithPaidInFull.getFile(), StandardCharsets.UTF_8)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(FileUtils.readFileToString(responsePaymentFlexilibilityError.getFile(), StandardCharsets.UTF_8)));
    }

    @Test
    public void ShouldReturnWithdrawalPastOverdraftConstraintsError() throws Exception {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(String.class), any(String.class))).thenReturn(Option.of(creditsEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(flexibilitySdk.paymentLoan(any(PaymentRequest.class))).thenThrow(new ProviderException("WITHDRAWAL_PAST_OVERDRAFT_CONSTRAINTS", "450"));
        mockMvc.perform(MockMvcRequestBuilders
                .post(TESTED_URL, ID_CLIENT)
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(FileUtils.readFileToString(requestPaymentInstallmentWithPaidInFull.getFile(), StandardCharsets.UTF_8)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(FileUtils.readFileToString(requestPaymentInstallmentWithdrawalPast.getFile(), StandardCharsets.UTF_8)));
    }

    @Test
    public void ShouldReturnWithdrBalanceBelowZeroError() throws Exception {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(String.class), any(String.class))).thenReturn(Option.of(creditsEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(flexibilitySdk.paymentLoan(any(PaymentRequest.class))).thenThrow(new ProviderException("BALANCE_BELOW_ZERO", "401"));
        mockMvc.perform(MockMvcRequestBuilders
                .post(TESTED_URL, ID_CLIENT)
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(FileUtils.readFileToString(requestPaymentInstallmentWithPaidInFull.getFile(), StandardCharsets.UTF_8)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(FileUtils.readFileToString(requestPaymentInstallmentWithdrawalPast.getFile(), StandardCharsets.UTF_8)));
    }

    @Test
    public void ShouldReturnMethodNotAllowedSinceSavingAccountBlocked() throws Exception {
        accounts.get(0).setState(SavingAccountState.LOCKED.name());
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(String.class), any(String.class))).thenReturn(Option.of(creditsEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        mockMvc.perform(MockMvcRequestBuilders
                .post(TESTED_URL, ID_CLIENT)
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(FileUtils.readFileToString(requestPaymentInstallmentWithPaidInFull.getFile(), StandardCharsets.UTF_8)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void shouldReturnForbiddenWhenIdClientDoesNotMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(TESTED_URL, UUID.randomUUID().toString())
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(paymentInstallment)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void shouldReturnForbiddenWhenIdClientDoesNotMatchWithCredit() throws Exception {
        creditsEntity.setIdClient(UUID.randomUUID().toString());
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(any(String.class), any(String.class))).thenReturn(Option.of(creditsEntity));

        mockMvc.perform(MockMvcRequestBuilders
                .post(TESTED_URL, ID_CLIENT)
                .with(getBearerToken())
                .contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(paymentInstallment)))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
