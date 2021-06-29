package com.lulobank.credits.services.utils;


import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.sdk.dto.payment.PaymentInstallment;
import com.lulobank.credits.services.domain.ClientDomain;
import com.lulobank.credits.services.features.payment.model.Currency;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanForClient;
import com.lulobank.credits.services.inboundadapters.model.LoanConditions;
import com.lulobank.credits.services.outboundadapters.model.ClientInformation;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.LoanConditionsEntity;
import com.lulobank.credits.services.outboundadapters.model.LoanRequested;
import com.lulobank.credits.services.outboundadapters.model.RiskEngineAnalysis;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import flexibility.client.models.request.PaymentRequest;
import flexibility.client.models.response.CreateClientResponse;
import flexibility.client.models.response.CreateLoanResponse;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class MapperBuilder {

    private MapperBuilder() {
    }


    public static ClientDomain buildClientDomainFromCreateLoanAccountRequest(CreateLoanForClient createLoanClientRequest, CreateClientResponse createClientResponse, CreateLoanResponse createLoanResponse) {
        ClientDomain clientDomain = new ClientDomain();
        clientDomain.setEmailAddress(createLoanClientRequest.getEmailAddress());
        clientDomain.setFirstName(createLoanClientRequest.getFirstName());
        clientDomain.setGender(createLoanClientRequest.getGender());
        clientDomain.setHomePhone(createLoanClientRequest.getMobilePhone());
        clientDomain.setLastName(createLoanClientRequest.getLastName());
        clientDomain.setMiddleName(createLoanClientRequest.getMiddleName());
        clientDomain.setMobilePhone1(createLoanClientRequest.getMobilePhone());
        clientDomain.setMobilePhone2(createLoanClientRequest.getMobilePhone());
        clientDomain.setAccountHolderType("CLIENT");
        clientDomain.setCreateClientResponse(createClientResponse);
        clientDomain.setCreateLoanResponse(createLoanResponse);
        clientDomain.setAssignedBranchKey(ProductTypeMambu.ASSIGNED_BRANCH_KEY.getKey());
        clientDomain.setIdClientMambu(createClientResponse.getClient().getId());
        return clientDomain;
    }

    public static List<LoanConditionsEntity> buildLoanConditionsEntity(List<LoanConditions> listLoanConditions) {
        return listLoanConditions.stream().map(
                loanConditions -> new LoanConditionsEntity(
                        loanConditions.getAmount(),
                        loanConditions.getInterestRate(),
                        loanConditions.getDefaultRate(),
                        loanConditions.getInstallments(),
                        loanConditions.getMaxAmountInstallment(),
                        loanConditions.getType()
                )
        ).collect(Collectors.toList());
    }

    public static ClientInformation clientInformationFromRequest(GetOfferToClient getOfferToClient) {
        return new ModelMapper().map(getOfferToClient.getClientInformation(), ClientInformation.class);
    }

    public static LoanRequested loanRequestedFromRequest(GetOfferToClient getOfferToClient) {
        LoanRequested loanRequested = new LoanRequested();
        loanRequested.setAmount(getOfferToClient.getClientLoanRequestedAmount());
        loanRequested.setPurpose(getOfferToClient.getLoanPurpose());
        return loanRequested;
    }

    public static RiskEngineAnalysis riskEngineAnalysisFromRequest(GetOfferToClient getOfferToClient) {
        return new ModelMapper().map(getOfferToClient.getRiskEngineAnalysis(), RiskEngineAnalysis.class);
    }

    public static com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.ClientInformation clientInformationSavingAccountFromEntity(CreditsEntity creditsEntity) {
        return new ModelMapper().map(creditsEntity.getClientInformation(),
                com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.ClientInformation.class);
    }

    public static PaymentRequest getPaymentRequest(PaymentInstallment paymentInstallment, CreditsV3Entity creditsEntity) {
        PaymentRequest paymentRequest = new PaymentRequest();
        PaymentRequest.Amount amount = new PaymentRequest.Amount();
        PaymentRequest.Account account = new PaymentRequest.Account();
        amount.setAmount(paymentInstallment.getAmount());
        amount.setCurrency(Currency.CO.name());
        paymentRequest.setAmount(amount);
        account.setNumber(creditsEntity.getIdSavingAccount());
        paymentRequest.setClientId(creditsEntity.getIdClientMambu());
        paymentRequest.setLoanAccountId(creditsEntity.getIdLoanAccountMambu());
        paymentRequest.setAccount(account);
        paymentRequest.setPayOff(paymentInstallment.getPaidInFull());
        return paymentRequest;
    }
}
