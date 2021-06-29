package com.lulobank.credits.starter.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableList;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanRequestV3;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.InitialOffer;
import com.lulobank.credits.services.outboundadapters.model.LoanStatus;
import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import com.lulobank.credits.starter.v3.adapters.in.CreditWithOfferV3Request;
import com.lulobank.credits.starter.v3.adapters.in.dto.ApprovedProductOffer;
import com.lulobank.credits.starter.v3.adapters.in.dto.CustomPaymentRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.MinimumPaymentRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.OfferInstallment;
import com.lulobank.credits.starter.v3.adapters.in.dto.PaymentPlanRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.PaymentPlanResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.PreapprovedLoanOffersResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.TotalPaymentRequest;
import com.lulobank.credits.starter.v3.adapters.in.payment.dto.PaymentRequest;
import com.lulobank.credits.v3.dto.ClientInformationV3;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.dto.DecevalInformationV3;
import com.lulobank.credits.v3.dto.DocumentIdV3;
import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.in.loan.dto.LoanResponse;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePaymentRequest;
import com.lulobank.credits.v3.port.in.productoffer.dto.Offer;
import com.lulobank.credits.v3.port.in.productoffer.dto.ProductOffer;
import com.lulobank.credits.v3.port.in.productoffer.dto.SimulatedInstallment;
import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteRequest;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.corebanking.dto.CreatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.TypePayment;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.credits.v3.usecase.payment.dto.PaymentType;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.dto.OfferedResponse;
import com.lulobank.promissorynote.sdk.dto.CreatePromissoryNoteClientAndSignResponse;
import com.lulobank.savingsaccounts.sdk.dto.createsavingaccountv3.SavingAccountCreated;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.CreateSavingsAccountResponse;
import flexibility.client.enums.LoanAccountPaymentStatus;
import flexibility.client.models.response.CreateLoanResponse;
import flexibility.client.models.response.GetLoanResponse;
import flexibility.client.models.response.GetLoanStatementResponse;
import flexibility.client.models.response.GetLoanStatementResponse.LoanData;
import flexibility.client.models.response.PaymentResponse;
import flexibility.client.models.response.SimulatedLoanResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum.LATE;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum.PENDING;
import static com.lulobank.credits.starter.utils.Constants.ACCOUNT_ID;
import static com.lulobank.credits.starter.utils.Constants.AMOUNT;
import static com.lulobank.credits.starter.utils.Constants.AMOUNT_INSTALLMENT;
import static com.lulobank.credits.starter.utils.Constants.AMOUNT_SIMULATE;
import static com.lulobank.credits.starter.utils.Constants.CLIENT_ACCOUNT_ID;
import static com.lulobank.credits.starter.utils.Constants.DAY_OF_PAY;
import static com.lulobank.credits.starter.utils.Constants.DECEVAL_CLIENT_ACCOUNT_ID;
import static com.lulobank.credits.starter.utils.Constants.DECEVAL_CONFIRMATION_LOAN_OTP;
import static com.lulobank.credits.starter.utils.Constants.DECEVAL_CORRELATION_ID;
import static com.lulobank.credits.starter.utils.Constants.DECEVAL_PROMISSORY_NOTE_ID;
import static com.lulobank.credits.starter.utils.Constants.DEFAULT_CURRENCY;
import static com.lulobank.credits.starter.utils.Constants.EMAIL;
import static com.lulobank.credits.starter.utils.Constants.FEES_DUE;
import static com.lulobank.credits.starter.utils.Constants.ID_CARD;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT_MAMBU;
import static com.lulobank.credits.starter.utils.Constants.ID_CREDIT;
import static com.lulobank.credits.starter.utils.Constants.ID_LOAN;
import static com.lulobank.credits.starter.utils.Constants.ID_LOAN_ACCOUNT_MAMBU;
import static com.lulobank.credits.starter.utils.Constants.ID_OFFER;
import static com.lulobank.credits.starter.utils.Constants.ID_PRODUCT_OFFER;
import static com.lulobank.credits.starter.utils.Constants.INSTALLMENT;
import static com.lulobank.credits.starter.utils.Constants.INTEREST_DUE;
import static com.lulobank.credits.starter.utils.Constants.INTEREST_RATE;
import static com.lulobank.credits.starter.utils.Constants.LAST_NAME;
import static com.lulobank.credits.starter.utils.Constants.NAME;
import static com.lulobank.credits.starter.utils.Constants.OTP_NUMBER;
import static com.lulobank.credits.starter.utils.Constants.PASSWORD;
import static com.lulobank.credits.starter.utils.Constants.PERCENT_FEES_DUE;
import static com.lulobank.credits.starter.utils.Constants.PERCENT_INTEREST_DUE;
import static com.lulobank.credits.starter.utils.Constants.PERCENT_PRINCIPAL_DUE;
import static com.lulobank.credits.starter.utils.Constants.PRODUCT_ID;
import static com.lulobank.credits.starter.utils.Constants.PRODUCT_TYPE_KEY;
import static com.lulobank.credits.starter.utils.Constants.PROMISSORY_NOTE_ID;
import static com.lulobank.credits.starter.utils.Constants.TOTAL_BALANCE;
import static com.lulobank.credits.starter.utils.Constants.TOTAL_DUE;
import static com.lulobank.credits.starter.utils.Constants.TYPE_CARD;
import static com.lulobank.credits.v3.service.OffersTypeV3.COMFORTABLE_LOAN;
import static org.springframework.util.ResourceUtils.getFile;

;

public class Samples {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Double DOUBLE_VALUE = 0.0D;

    public static CreditWithOfferV3Request creditWithOfferV3RequestBuilder() {
        CreditWithOfferV3Request creditWithOfferV3Request = new CreditWithOfferV3Request();
        creditWithOfferV3Request.setIdCredit(ID_CREDIT);
        creditWithOfferV3Request.setConfirmationLoanOTP("1111");
        return creditWithOfferV3Request;
    }

    public static LoanRequest loanRequestBuilder() {
        return LoanRequest.builder().clientId(ID_CLIENT).productTypeKey(PRODUCT_TYPE_KEY).repaymentInstallments(12)
                .build();
    }

    public static CreditsEntity creditsEntityBuilder() {
        CreditsEntity creditsDto = new CreditsEntity();
        creditsDto.setIdClient(ID_CLIENT);
        creditsDto.setIdCredit(UUID.fromString(ID_CREDIT));
        creditsDto.setIdLoanAccountMambu(ID_LOAN_ACCOUNT_MAMBU);
        creditsDto.setInitialOffer(new InitialOffer());
        OfferEntity offerEntity = new OfferEntity();
        offerEntity.setIdOffer(ID_OFFER);
        offerEntity.setType(COMFORTABLE_LOAN.name());
        creditsDto.getInitialOffer().setOfferEntities(ImmutableList.of(offerEntity));
        LoanStatus loanStatus = new LoanStatus();
        loanStatus.setStatus("ACTIVE");
        creditsDto.setLoanStatus(loanStatus);
        return creditsDto;
    }


    public static CreditsV3Entity creditsV3EntityBuilder() {
        CreditsV3Entity creditsV3Entity = new CreditsV3Entity();
        creditsV3Entity.setIdClient(ID_CLIENT);
        creditsV3Entity.setIdClientMambu(ID_LOAN);
        creditsV3Entity.setIdCredit(UUID.fromString(ID_CREDIT));
        creditsV3Entity.setClientInformation(clientInformationV3Builder());
        creditsV3Entity.setDecevalInformation(decevalInformationBuilder());
        creditsV3Entity.setAcceptOffer(offerEntityV3Builder());
        creditsV3Entity.setInitialOffer(initialOfferBuilder());
        creditsV3Entity.setIdLoanAccountMambu("92377900292");
        creditsV3Entity.setAutomaticDebit(true);
        creditsV3Entity.setDayOfPay(DAY_OF_PAY);
        creditsV3Entity.setAcceptDate(LocalDateTime.now());
        return creditsV3Entity;
    }

    private static InitialOfferV3 initialOfferBuilder() {
        InitialOfferV3 initialOfferV3 = new InitialOfferV3();
        initialOfferV3.setAmount(AMOUNT);
        return initialOfferV3;
    }

    private static DecevalInformationV3 decevalInformationBuilder() {
        DecevalInformationV3 decevalInformationV3 = new DecevalInformationV3();
        decevalInformationV3.setClientAccountId(DECEVAL_CLIENT_ACCOUNT_ID);
        decevalInformationV3.setConfirmationLoanOTP(DECEVAL_CONFIRMATION_LOAN_OTP);
        decevalInformationV3.setDecevalCorrelationId(DECEVAL_CORRELATION_ID);
        decevalInformationV3.setPromissoryNoteId(DECEVAL_PROMISSORY_NOTE_ID);
        return decevalInformationV3;
    }

    public static PromissoryNoteRequest promissoryNoteRequestBuilder(DocumentIdV3 documentIdV3) {
        PromissoryNoteRequest promissoryNoteRequest = new PromissoryNoteRequest();
        promissoryNoteRequest.setDocumentId(documentIdV3);
        promissoryNoteRequest.setName(NAME);
        promissoryNoteRequest.setEmail(EMAIL);
        return promissoryNoteRequest;
    }

    public static DocumentIdV3 documentIdV3Builder() {
        DocumentIdV3 documentIdV3 = new DocumentIdV3();
        documentIdV3.setId(ID_CARD);
        documentIdV3.setType(TYPE_CARD);
        return documentIdV3;
    }

    public static CreatePromissoryNoteClientAndSignResponse createPromissoryNoteClientAndSignResponseBuilder() {
        CreatePromissoryNoteClientAndSignResponse createPromissoryNoteClientAndSignResponse = new CreatePromissoryNoteClientAndSignResponse();
        createPromissoryNoteClientAndSignResponse.setClientAccountId(CLIENT_ACCOUNT_ID);
        createPromissoryNoteClientAndSignResponse.setPromissoryNoteId(PROMISSORY_NOTE_ID);
        createPromissoryNoteClientAndSignResponse.setSignPassword(PASSWORD);
        return createPromissoryNoteClientAndSignResponse;
    }

    public static SavingsAccountRequest savingsAccountRequestBuilder(ClientInformationV3 clientInformationV3) {
        SavingsAccountRequest savingsAccountRequest = new SavingsAccountRequest();
        savingsAccountRequest.setIdClient(ID_CLIENT);
        savingsAccountRequest.setClientInformation(clientInformationV3);
        return savingsAccountRequest;
    }

    public static ClientInformationV3 clientInformationV3Builder() {
        ClientInformationV3 clientInformationV3 = new ClientInformationV3();
        clientInformationV3.setDocumentId(Samples.documentIdV3Builder());
        clientInformationV3.setEmail(EMAIL);
        clientInformationV3.setName(NAME);
        clientInformationV3.setLastName(LAST_NAME);
        return clientInformationV3;
    }

    public static CreateSavingsAccountResponse createSavingsAccountResponseBuilder() {
        CreateSavingsAccountResponse createSavingsAccountResponse = new CreateSavingsAccountResponse();
        createSavingsAccountResponse.setIdCbs(ID_LOAN);
        createSavingsAccountResponse.setAccountId(ACCOUNT_ID);
        return createSavingsAccountResponse;
    }

    public static SavingAccountCreated createSavingAccountCreated() {
        SavingAccountCreated savingAccountCreated = new SavingAccountCreated();
        savingAccountCreated.setAccountId(ACCOUNT_ID);
        savingAccountCreated.setIdCbs(ID_LOAN);
        return savingAccountCreated;
    }

    public static CreateLoanResponse createLoanResponseBuilder() {
        CreateLoanResponse createLoanResponse = new CreateLoanResponse();
        createLoanResponse.setId(ID_LOAN);
        createLoanResponse.setProductTypeKey(Constants.PRODUCT_TYPE_KEY);
        return createLoanResponse;
    }

    public static AcceptOffer acceptOfferBuilder() {
        AcceptOffer acceptOffer = new AcceptOffer();
        acceptOffer.setIdClient(ID_CLIENT);
        acceptOffer.setIdCredit(ID_CREDIT);
        acceptOffer.setConfirmationLoanOTP(OTP_NUMBER);
        return acceptOffer;
    }

    public static OfferEntityV3 offerEntityV3Builder() {
        OfferEntityV3 offerEntityV3 = new OfferEntityV3();
        offerEntityV3.setAmount(AMOUNT);
        offerEntityV3.setInstallments(INSTALLMENT);
        offerEntityV3.setInterestRate(BigDecimal.valueOf(INTEREST_RATE));
        return offerEntityV3;
    }

    public static LoanTransaction loanTransactionBuilder(SavingsAccountResponse savingsAccountResponse) {
        LoanTransaction loanTransaction = new LoanTransaction();
        loanTransaction.setSavingsAccountResponse(savingsAccountResponse)
                .setLoanResponse(laonResponseBuilder())
                .setCreditsV3Entity(creditsV3EntityBuilder());
        return loanTransaction;
    }

    public static LoanResponse laonResponseBuilder() {
        LoanResponse loanResponse = new LoanResponse();
        loanResponse.setId(ID_LOAN);
        return loanResponse;
    }

    public static SavingsAccountResponse savingsAccountResponseBuilder() {
        SavingsAccountResponse savingsAccountResponse = new SavingsAccountResponse();
        savingsAccountResponse.setAccountId(ACCOUNT_ID);
        savingsAccountResponse.setIdCbs(ID_CLIENT_MAMBU);
        return savingsAccountResponse;
    }

    public static SimulatePaymentRequest simulatePaymentRequestBuilder() {
        SimulatePaymentRequest simulatePaymentRequest = new SimulatePaymentRequest();
        simulatePaymentRequest.setInterestRate(BigDecimal.valueOf(INTEREST_RATE));
        simulatePaymentRequest.setDayOfPay(DAY_OF_PAY);
        simulatePaymentRequest.setAmount(AMOUNT_SIMULATE.doubleValue());
        return simulatePaymentRequest;
    }

    public static SimulatedLoanResponse.Repayment repaymentBuilder() {
        SimulatedLoanResponse.Repayment repayment = new SimulatedLoanResponse.Repayment();
        repayment.setTotalDue(Constants.AMOUNT_INSTALLMENT);
        repayment.setInterestDue(Constants.INTEREST);
        return repayment;
    }

    public static CreditsConditionV3 credistConditionV3Builder() {
        CreditsConditionV3 creditsConditionV3 = new CreditsConditionV3();
        creditsConditionV3 = new CreditsConditionV3();
        creditsConditionV3.setLoanProductId(PRODUCT_ID);
        creditsConditionV3.setDefaultCurrency(DEFAULT_CURRENCY);
        return creditsConditionV3;
    }

    public static CreditsEntity creditsOffersEntityBuilder() throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(getFile("classpath:mocks/v3/Credits-Offer-Entity.json"), CreditsEntity.class);
    }

    public static PaymentV3 paymentV3Builder() {
        PaymentV3 paymentV3 = new PaymentV3();
        paymentV3.setDueDate(LocalDate.now());
        paymentV3.setTotalDue(TOTAL_DUE);
        paymentV3.setFeesDue(FEES_DUE);
        paymentV3.setInterestDue(INTEREST_DUE);
        paymentV3.setInstallment(1);
        paymentV3.setPercentInterestDue(PERCENT_INTEREST_DUE);
        paymentV3.setPercentPrincipalDue(PERCENT_PRINCIPAL_DUE);
        paymentV3.setPercentFeesDue(PERCENT_FEES_DUE);
        return paymentV3;
    }

    public static PaymentPlanRequestV3 paymentPlanRequestV3Builder() {
        PaymentPlanRequestV3 request = new PaymentPlanRequestV3();
        request.setIdCredit(Constants.ID_CREDIT);
        request.setIdClient(Constants.ID_CLIENT);
        request.setDayOfPay(Constants.DAY_OF_PAY);
        request.setIdOffer(Constants.ID_OFFER);
        return request;
    }

    public static LoanTransaction loanTransactionBuilder() {
        LoanTransaction loanTransaction = new LoanTransaction();
        loanTransaction.setLoanResponse(laonResponseBuilder()).setCreditsV3Entity(creditsV3EntityBuilder());
        return loanTransaction;
    }

    public static ProductOfferResponse buildProductOfferResponse() {
        return ProductOfferResponse.builder()
                .amount(11000000d)
                .currentDate(LocalDateTime.now())
                .idCredit(ID_CREDIT)
                .offers(ImmutableList.of(buildApprovedOffer()))
                .build();
    }

    public static ApprovedProductOffer buildApprovedOffer() {
        return ApprovedProductOffer.builder()
                .amount(11000000d)
                .idOffer(UUID.randomUUID().toString())
                .interestRate(16.5f)
                .insuranceCost(0.0026d)
                .type("FLEXIBLE_LOAN")
                .name("Crédito personalizado")
                .monthlyNominalRate(1.281f)
                .simulateInstallment(buildInstallments(10))
                .build();
    }

    public static List<OfferInstallment> buildInstallments(int listSize) {
        return IntStream.range(0, listSize)
                .mapToObj(i -> new OfferInstallment(i + 1, 30000d))
                .collect(Collectors.toList());
    }

    public static ProductOfferRequest buildRequest() {
        ProductOfferRequest request = new ProductOfferRequest();
        request.setAmount(11000000d);
        request.setLoanPurpose("Trip");
        request.setIdProductOffer(ID_PRODUCT_OFFER);
        return request;
    }

    public static ProductOffer buildProductOffer() {
        ProductOffer productOffer = new ProductOffer();
        productOffer.setIdCredit(ID_CREDIT);
        productOffer.setAmount(11000000d);
        productOffer.setCurrentDate(LocalDateTime.now());
        productOffer.setOffers(ImmutableList.of(buildOffer()));
        return productOffer;
    }

    public static Offer buildOffer() {
        return Offer.builder()
                .amount(11000000d)
                .idOffer(UUID.randomUUID().toString())
                .interestRate(16.5f)
                .insuranceCost(0.0026d)
                .type("FLEXIBLE_LOAN")
                .name("Crédito personalizado")
                .monthlyNominalRate(1.281f)
                .simulateInstallment(buildInstallmentList(10))
                .build();
    }

    public static List<SimulatedInstallment> buildInstallmentList(int listSize) {
        return IntStream.range(0, listSize)
                .mapToObj(i -> new SimulatedInstallment(i + 1, 30000d))
                .collect(Collectors.toList());
    }

    public static ProductOfferRequest buildProductOfferRequest() {
        ProductOfferRequest request = new ProductOfferRequest();
        request.setAmount(11000000d);
        request.setLoanPurpose("Trip");
        request.setIdProductOffer(ID_PRODUCT_OFFER);
        return request;
    }

    public static PaymentResponse getPaymentResponse() {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setAmount(String.valueOf(AMOUNT));
        paymentResponse.setStatus("ACTIVE");
        paymentResponse.setEntryDate(LocalDateTime.now().toString());
        paymentResponse.setTransactionId("transaction-id");
        return paymentResponse;
    }

    public static CreatePayment createPayment() {
        return CreatePayment.builder()
        .loanId(ID_LOAN)
        .amount(BigDecimal.valueOf(AMOUNT))
        .coreBankingId(ID_CLIENT_MAMBU)
        .accountId(ACCOUNT_ID)
        .type(TypePayment.NONE)
        .build();
    }

    public static MinimumPaymentRequest paymentMinimumRequest(){
        MinimumPaymentRequest minimumPaymentRequest = new MinimumPaymentRequest();
        minimumPaymentRequest.setAmount(BigDecimal.valueOf(AMOUNT));
        minimumPaymentRequest.setIdCredit(ID_CREDIT);
        minimumPaymentRequest.setIdCreditCBS(ID_LOAN);
        return minimumPaymentRequest;
    }

    public static CustomPaymentRequest paymentCustomRequest(){
        CustomPaymentRequest customPaymentRequest = new CustomPaymentRequest();
        customPaymentRequest.setAmount(BigDecimal.valueOf(AMOUNT));
        customPaymentRequest.setIdCredit(ID_CREDIT);
        customPaymentRequest.setIdCreditCBS(ID_LOAN);
        customPaymentRequest.setType("AMOUNT_INSTALLMENTS");
        return customPaymentRequest;
    }

    public static TotalPaymentRequest totalMinimumRequest(){
        TotalPaymentRequest totalPaymentRequest = new TotalPaymentRequest();
        totalPaymentRequest.setAmount(BigDecimal.valueOf(AMOUNT));
        totalPaymentRequest.setIdCredit(ID_CREDIT);
        totalPaymentRequest.setIdCreditCBS(ID_LOAN);
        return totalPaymentRequest;
    }

    public static PaymentRequest paymentRequest(){
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(BigDecimal.valueOf(AMOUNT));
        paymentRequest.setIdCredit(ID_CREDIT);
        paymentRequest.setPaymentType(PaymentType.MINIMUM_PAYMENT);
        return paymentRequest;
    }

    public static GetLoanResponse buildGetLoanResponse(){
        GetLoanResponse getLoanResponse = new GetLoanResponse();
        GetLoanResponse.Balance balance = new GetLoanResponse.Balance();
        balance.setAmount(TOTAL_BALANCE);
        getLoanResponse.setTotalBalance(balance);
        getLoanResponse.setPaymentPlanItemApiList(getPaymentPlanItems());
        GetLoanResponse.LoanAmount loanAmount = new GetLoanResponse.LoanAmount();
        getLoanResponse.setCutOffDate(LocalDate.now());
        loanAmount.setAmount(AMOUNT);
        getLoanResponse.setInstallmentTotalDue(loanAmount);
        GetLoanResponse.LoanAmount expectedDue = new GetLoanResponse.LoanAmount();
        expectedDue.setAmount(3000000d);
        expectedDue.setCurrency("COP");
        getLoanResponse.setInstallmentExpectedDue(expectedDue);
        getLoanResponse.setInstallmentExpected(expectedDue);
        getLoanResponse.setInstallmentAccrued(expectedDue);
        return getLoanResponse;
    }
    
    public static GetLoanStatementResponse buildGetLoanStatementResponse() {
		GetLoanStatementResponse getLoanStatementResponse = new GetLoanStatementResponse();
		getLoanStatementResponse.setInstalments(2);
		getLoanStatementResponse.setLoanData(buildLoanData());
        getLoanStatementResponse.setPreviousLoanPeriodData(buildLoanData());
		getLoanStatementResponse.setInArrearsBalance(DOUBLE_VALUE);
		getLoanStatementResponse.setTotalBalance(DOUBLE_VALUE);
		getLoanStatementResponse.setPrincipalPaid(DOUBLE_VALUE);
		getLoanStatementResponse.setLoanAmount(DOUBLE_VALUE);
		getLoanStatementResponse.setDisbursementDate(LocalDateTime.now());
		getLoanStatementResponse.setPenaltyRate(DOUBLE_VALUE);
		getLoanStatementResponse.setAmortization("");
		getLoanStatementResponse.setAccruedPenalty(DOUBLE_VALUE);
		getLoanStatementResponse.setPenaltyBalance(DOUBLE_VALUE);
		getLoanStatementResponse.setDaysInArrears(5L);
		getLoanStatementResponse.setState(LoanAccountPaymentStatus.IN_ARREARS);
        getLoanStatementResponse.setInterestRate(DOUBLE_VALUE);
		return getLoanStatementResponse;
	}

	private static LoanData buildLoanData() {
		LoanData loanData = new LoanData();
		loanData.setCutOffDate(LocalDate.now());
		loanData.setInstalmentDueDate(LocalDate.now());
		loanData.setInstalmentTotalDue(DOUBLE_VALUE);
		loanData.setInstalmentPrincipalDue(DOUBLE_VALUE);
		loanData.setInstalmentInterestDue(DOUBLE_VALUE);
		loanData.setInstalmentPenaltiesDue(DOUBLE_VALUE);
		loanData.setFeesAmount(DOUBLE_VALUE);
		loanData.setLegalExpenses(DOUBLE_VALUE);
		loanData.setInstalment(3);
		loanData.setTotalPaid(DOUBLE_VALUE);
		loanData.setPrincipalPaid(DOUBLE_VALUE);
		loanData.setInterestPaid(DOUBLE_VALUE);
		loanData.setPenaltyPaid(DOUBLE_VALUE);
		loanData.setFeesPaid(DOUBLE_VALUE);
		return loanData;
	}

    private static List<GetLoanResponse.PaymentPlanItem> getPaymentPlanItems() {
        List<GetLoanResponse.PaymentPlanItem> paymentPlanItemList = new ArrayList<>();
        paymentPlanItemList.add(getPaymentPlanItem(LATE.name(), LocalDateTime.now(),1000d));
        paymentPlanItemList.add(getPaymentPlanItem(PENDING.name(), LocalDateTime.now().plusMonths(1), 2000d));
        paymentPlanItemList.add(getPaymentPlanItem(PENDING.name(), LocalDateTime.now().plusMonths(1), 0d));
        paymentPlanItemList.add(getPaymentPlanItem(PENDING.name(), LocalDateTime.now().plusMonths(1), 0d));
        return paymentPlanItemList;
    }

    private static GetLoanResponse.PaymentPlanItem getPaymentPlanItem(String state, LocalDateTime localDateTime,
                                                               Double totalDue) {
        GetLoanResponse.PaymentPlanItem paymentPlanItem = new GetLoanResponse.PaymentPlanItem();
        paymentPlanItem.setState(state);
        paymentPlanItem.setDueDate(localDateTime);
        paymentPlanItem.setCutOffDate(localDateTime.minusDays(10).toLocalDate());
        paymentPlanItem.setTotalDue(totalDue);
        paymentPlanItem.setLastPaidDate(localDateTime.plusMonths(1L));
        return paymentPlanItem;
    }

    public static PreapprovedLoanOffersResponse preapprovedLoanOffersResponse() throws IOException {
        return PreapprovedLoanOffersResponse.builder()
                .amount(BigDecimal.valueOf(AMOUNT))
                .maxAmountInstallment(BigDecimal.valueOf(AMOUNT_INSTALLMENT))
                .idCredit(ID_CREDIT)
                .build();
    }

    public static OfferedResponse offeredResponse() throws IOException {
        return OfferedResponse.builder()
                .amount(BigDecimal.valueOf(AMOUNT))
                .maxAmountInstallment(BigDecimal.valueOf(AMOUNT_INSTALLMENT))
                .idCredit(ID_CREDIT)
                .build();
    }

    public static PaymentPlanResponse paymentPlanResponse() throws IOException {
        return PaymentPlanResponse.builder()
                .endDate(LocalDate.now())
                .startDate(LocalDate.now())
                .principalDebit(BigDecimal.valueOf(AMOUNT_INSTALLMENT))
                .build();
    }

    public static PaymentPlanRequest paymentPlanRequest() {
        PaymentPlanRequest paymentPlanRequest = new PaymentPlanRequest();
        paymentPlanRequest.setDayOfPay(5);
        paymentPlanRequest.setIdCredit(UUID.randomUUID().toString());
        paymentPlanRequest.setIdOffer(UUID.randomUUID().toString());
        paymentPlanRequest.setInstallments(15);
        return paymentPlanRequest;
    }
}
