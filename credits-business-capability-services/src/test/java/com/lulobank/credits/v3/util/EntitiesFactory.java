package com.lulobank.credits.v3.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.OfferV3;
import com.lulobank.credits.v3.port.in.digitalevidence.DigitalEvidenceCreatedMessage;
import com.lulobank.credits.v3.port.in.loan.dto.LoanResponse;
import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteResponse;
import com.lulobank.credits.v3.port.in.rescheduledloan.RescheduledLoanMessage;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.corebanking.dto.ClientAccount;
import com.lulobank.credits.v3.port.out.corebanking.dto.CreatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.service.dto.LoanPaymentRequest;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.usecase.automaticdebit.command.ProcessPayment;
import com.lulobank.credits.v3.usecase.automaticdebitoption.dto.UpdateAutomaticDebitOption;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.credits.v3.usecase.intialsoffersv3.command.GetOffersByClient;
import com.lulobank.credits.v3.usecase.movement.dto.GetMovements;
import com.lulobank.credits.v3.usecase.payment.dto.Payment;
import com.lulobank.credits.v3.usecase.payment.dto.PaymentType;
import com.lulobank.credits.v3.usecase.payment.dto.SubPaymentType;
import com.lulobank.credits.v3.vo.AdapterCredentials;
import io.vavr.control.Try;
import org.modelmapper.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.lulobank.credits.Constants.ACCOUNT_BALANCE;
import static com.lulobank.credits.Constants.CLIENT_ID;
import static com.lulobank.credits.Constants.LIMIT;
import static com.lulobank.credits.Constants.LOAN_AMOUNT;
import static com.lulobank.credits.Constants.MAMBU_ACCOUNT_ID;
import static com.lulobank.credits.Constants.OFFSET;
import static com.lulobank.credits.Constants.SAVINGS_ACCOUNT_ID;
import static com.lulobank.credits.services.Constant.*;
import static org.springframework.util.ResourceUtils.getFile;

public class EntitiesFactory {


    public static class AcceptOfferFactory {

        public static AcceptOffer createAcceptOfferWithOfferValid() {
            AcceptOffer acceptOffer = new AcceptOffer();
            acceptOffer.setAutomaticDebitPayments(false);
            acceptOffer.setConfirmationLoanOTP("OO14");
            acceptOffer.setDayOfPay(3);
            acceptOffer.setIdClient("11222-232323");
            acceptOffer.setIdCredit(UUID.randomUUID().toString());
            acceptOffer.setSelectedCredit(OfferFactory.createOfferValid());
            Map<String, String> authHeaders = new HashMap<>();

            authHeaders.put("MockToken", "TokenValue");
            acceptOffer.setCredentials(new AdapterCredentials(authHeaders));
            return acceptOffer;
        }

        public static AcceptOffer createAcceptOfferWithOfferInvalid() {
            AcceptOffer acceptOffer = new AcceptOffer();
            acceptOffer.setAutomaticDebitPayments(false);
            acceptOffer.setConfirmationLoanOTP("OO14");
            acceptOffer.setDayOfPay(3);
            acceptOffer.setIdClient("11222-232323");
            acceptOffer.setIdCredit(UUID.randomUUID().toString());
            acceptOffer.setSelectedCredit(OfferFactory.createOfferInvalid());
            return acceptOffer;
        }
    }

    public static class OfferFactory {

        public static OfferV3 createOfferValid() {
            return getObjectByFile("classpath:json/OfferValid.json", OfferV3.class);
        }

        public static OfferV3 createOfferInvalid() {
            return getObjectByFile("classpath:json/OfferInvalid.json", OfferV3.class);
        }

        public static OfferEntityV3 createOfferEntityV3Valid() {
            return getObjectByFile("classpath:json/v3/OfferEntityV3.json", OfferEntityV3.class);
        }

        public static OfferEntityV3 createOfferEntityFlexibleV3Valid() {
            return getObjectByFile("classpath:json/v3/OfferEntityFlexibleV3.json", OfferEntityV3.class);
        }
    }

    public static class CreditsEntityFactory {

        public static CreditsV3Entity foundCreditsEntityInBD() {
            return getObjectByFile("classpath:json/CreditsEntity.json", CreditsV3Entity.class);
        }

        public static CreditsV3Entity creditsEntityWithAcceptOffer() {
            return getObjectByFile("classpath:json/CreditsEntityAcceptOffer.json", CreditsV3Entity.class);
        }

        public static CreditsV3Entity creditsEntityOfferPreApproved() {
            return getObjectByFile("classpath:json/CreditsEntityPreApproved.json", CreditsV3Entity.class);
        }

        public static CreditsV3Entity creditsEntitWithRiskResponsev2() {
            return getObjectByFile("classpath:json/CreditsEntityWithRiskResultv2.json", CreditsV3Entity.class);
        }

        public static CreditsV3Entity creditsEntityAlreadyRescheduled() {
            return getObjectByFile("classpath:json/CreditsEntityAlreadyRescheduled.json", CreditsV3Entity.class);
        }

        public static CreditsV3Entity creditsEntityDisabledAutomaticDebit() {
            return getObjectByFile("classpath:json/CreditsEntityDisabledAutomaticDebit.json", CreditsV3Entity.class);
        }
    }

    public static class PromissoryNodeFactoryTest {

        public static PromissoryNoteResponse createPromissoryNoteResponse() {
            PromissoryNoteResponse promissoryNoteResponse = new PromissoryNoteResponse();
            promissoryNoteResponse.setClientAccountId(120);
            promissoryNoteResponse.setPromissoryNoteId(123);
            promissoryNoteResponse.setSignPassword("Sing");

            return promissoryNoteResponse;
        }
    }

    public static class SavingsAccountFactory {
        public static SavingsAccountResponse createSavingsAccountResponse() {
            SavingsAccountResponse savingsAccountResponse = new SavingsAccountResponse();
            savingsAccountResponse.setAccountId("AccountId");
            savingsAccountResponse.setIdCbs("cbs");

            return savingsAccountResponse;
        }
    }

    public static class LoanFactory {
        public static LoanResponse createLoanResponse() {
            LoanResponse loanResponse = new LoanResponse();
            loanResponse.setLabel("Label");
            loanResponse.setId("AccountId");
            loanResponse.setAccountState("AccountState");
            loanResponse.setProductTypeKey("ProductKey");
            loanResponse.setSettlementAccountKey("AccountKey");


            return loanResponse;
        }

        public static GetMovements createGetMovements() {
            return GetMovements.builder().idClient(CLIENT_ID).offset(OFFSET).limit(LIMIT).build();
        }
    }

    public static class LoanTransactionFactory {
        public static LoanTransaction createLoanTransaction() {
            LoanTransaction loanTransaction = new LoanTransaction();
            loanTransaction.setCreditsV3Entity(CreditsEntityFactory.creditsEntityWithAcceptOffer())
                    .setLoanResponse(LoanFactory.createLoanResponse())
                    .setPromissoryNoteResponse(PromissoryNodeFactoryTest.createPromissoryNoteResponse())
                    .setSavingsAccountResponse(SavingsAccountFactory.createSavingsAccountResponse());
            return loanTransaction;
        }
    }

    public static class CreditsCondition {
        public static CreditsConditionV3 createCreditsCondition() {
            CreditsConditionV3 creditsConditionV3 = new CreditsConditionV3();
            creditsConditionV3.setDefaultCurrency("COP");
            creditsConditionV3.setCbsProductKeyType("cbsProducts");
            return creditsConditionV3;
        }
    }

    public static class DigitalEvidenceEvent {

        public static DigitalEvidenceCreatedMessage buildDigitalEvidenceCreatedMessage(boolean success) {
            DigitalEvidenceCreatedMessage digitalEvidenceCreatedMessage = new DigitalEvidenceCreatedMessage();
            digitalEvidenceCreatedMessage.setAccountId("accountId");
            digitalEvidenceCreatedMessage.setIdCbs("idCbs");
            digitalEvidenceCreatedMessage.setIdClient("idClient");
            digitalEvidenceCreatedMessage.setIdCredit("5a9c7d82-393c-4c05-9b3f-35adea480f16");
            digitalEvidenceCreatedMessage.setSuccess(success);
            return digitalEvidenceCreatedMessage;
        }
    }

    public static class InitialsOfferFactory {

        public static InitialOfferV3 initialsOfferInDB() {
            return getObjectByFile("classpath:json/v3/InitialsOfferV3.json", InitialOfferV3.class);
        }

        public static GetOffersByClient initialsOfferRequest() {
            return getObjectByFile("classpath:json/v3/InitialsOffersRequest.json", GetOffersByClient.class);
        }
    }

    public static class SimulatePaymentFactory {

        public static SimulatePayment simulatePaymentResponse() {
            return getObjectByFile("classpath:json/v3/simulateLoan_request.json", SimulatePayment.class);
        }

        public static List<SimulatePayment> simulatePayments() {
            Type listType = new TypeToken<ArrayList<SimulatePayment>>(){}.getType();
            return getObjectByFile("classpath:json/v3/simultaePayments.json", listType);
        }
    }

    public static class OfferInformationRequestFactory {
        public static OfferInformationRequest.OfferInformationRequestBuilder OfferInformationRequestBuilder() {
            return OfferInformationRequest.builder()
                    .idClient(UUID.randomUUID().toString())
                    .clientLoanRequestedAmount(2000000d)
                    .interestRate(BigDecimal.valueOf(16.5f))
                    .loanAmount(2434568d)
                    .feeInsurance(0.0032)
                    .clientMonthlyAmountCapacity(890000d);
        }
    }

    public static class PaymentFactory {
        public static CreatePayment.CreatePaymentBuilder createPaymentBuilder() {
            return CreatePayment.builder()
                    .accountId(SAVINGS_ACCOUNT_ID)
                    .coreBankingId(MAMBU_ACCOUNT_ID)
                    .amount(LOAN_AMOUNT);
        }

        public static PaymentApplied.PaymentAppliedBuilder paymentResponseBuilder() {
            return PaymentApplied.builder()
                    .status("SUCCESS")
                    .amount(LOAN_AMOUNT);
        }

        public static ClientAccount.ClientAccountBuilder clientAccountBuilder() {
            return ClientAccount.builder()
                    .status("ACTIVE")
                    .number(SAVINGS_ACCOUNT_ID)
                    .balance(ACCOUNT_BALANCE);
        }

        public static LoanPaymentRequest.LoanPaymentRequestBuilder loanPaymentRequestBuilder() {
            return LoanPaymentRequest.builder()
                    .idCredit(ID_CREDIT)
                    .loanId(MAMBU_ACCOUNT_ID)
                    .amount(LOAN_AMOUNT)
                    .idClient(ID_CLIENT)
                    .type("NUMBER_INSTALLMENTS");

        }

        public static ProcessPayment processPayment(LoanInformation loanInformation, CreditsV3Entity creditsV3Entity, String metadata) {
            return ProcessPayment.builder()
                    .loanInformation(loanInformation)
                    .idCredit(UUID.randomUUID().toString())
                    .idClient(creditsV3Entity.getIdClient())
                    .idLoanAccountMambu(creditsV3Entity.getIdLoanAccountMambu())
                    .dayOfPay(creditsV3Entity.getDayOfPay())
                    .idCoreBanking(creditsV3Entity.getIdClientMambu())
                    .metadataEvent(metadata)
                    .build();
        }

        public static Payment.PaymentBuilder paymentCommand() {
            return Payment.builder().clientId(ID_CLIENT)
                    .creditId(ID_CREDIT)
                    .paymentType(PaymentType.MINIMUM_PAYMENT)
                    .amount(AMOUNT_PAYMENT)
                    .subPaymentType(SubPaymentType.NONE);
        }
    }

    public static class RescheduledLoanEventFactory {

        public static RescheduledLoanMessage okRescheduledLoan() {
            return getObjectByFile("classpath:json/rescheduledloan/RescheduledLoan.json",
                    RescheduledLoanMessage.class);
        }

        public static RescheduledLoanMessage okUpdatedLoan() {
            return getObjectByFile("classpath:json/rescheduledloan/UpdatedLoan.json", RescheduledLoanMessage.class);
        }
    }

    public static class AutomaticDebitFactory {

        public static UpdateAutomaticDebitOption updateAutomaticDebitOption(Boolean option) {
            return new UpdateAutomaticDebitOption(CLIENT_ID, option);
        }
    }

    private static <T> T getObjectByFile(String file, Class<T> clazz) {
        return getGson().fromJson(getContentFile(file), clazz);
    }

    private static <T> T getObjectByFile(String file, Type type) {
        return getGson().fromJson(getContentFile(file), type);
    }

    private static String getContentFile(String file) {
        return Try.of(() -> new BufferedReader(new FileReader(getFile(file).getPath())))
                .map(br -> br.lines().collect(Collectors.joining()))
                .get();
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Util.JsonDateTimeArrayDeserializer()).create();
    }
}
