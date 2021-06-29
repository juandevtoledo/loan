package com.lulobank.credits.services.outboundadapters.flexibility;

import com.lulobank.credits.sdk.dto.clientloandetail.Payment;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityLoanMovementsPaidStatesEnum;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.v3.dto.LoanConditionsEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import flexibility.client.models.request.GetLoanMovementsRequest;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.models.response.GetLoanMovementsResponse;
import flexibility.client.models.response.GetLoanResponse;
import flexibility.client.util.GetLoanMovementsRequestBuilder;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlexibilityMapper {

    private static final long MAX_NUMBER_OF_LOAN_MOVEMENTS = 5;

    private FlexibilityMapper() {
    }

    public static GetLoanRequest getLoanRequestFromCreditEntityInfo(CreditsEntity creditsEntity) {

        GetLoanRequest getLoanRequest = new GetLoanRequest();
        getLoanRequest.setClientId(creditsEntity.getIdClientMambu());
        getLoanRequest.setLoanId(creditsEntity.getIdLoanAccountMambu());
        return getLoanRequest;

    }

    public static GetLoanRequest getLoanRequestFromCreditV3EntityInfo(CreditsV3Entity creditsV3Entity) {
        GetLoanRequest getLoanRequest = new GetLoanRequest();
        getLoanRequest.setClientId(creditsV3Entity.getIdClientMambu());
        getLoanRequest.setLoanId(creditsV3Entity.getIdLoanAccountMambu());
        return getLoanRequest;

    }

    private static Double getInterestRate(CreditsV3Entity creditsV3Entity) {

        if (Objects.nonNull(creditsV3Entity.getInitialOffer())) {
            if (Objects.nonNull(creditsV3Entity.getInitialOffer().getRiskEngineAnalysis()))
                return creditsV3Entity.getInitialOffer().getRiskEngineAnalysis().getInterestRate().doubleValue();
            if (Objects.nonNull(creditsV3Entity.getInitialOffer().getInterestRate()))
                return creditsV3Entity.getInitialOffer().getInterestRate().doubleValue();
        }
        if (Objects.nonNull(creditsV3Entity.getLoanConditionsList())) {
            Optional<LoanConditionsEntityV3> first = creditsV3Entity.getLoanConditionsList().stream().findFirst();
            return  first.isPresent() ? first.get().getInterestRate().doubleValue() : null;
        }
        return null;
    }


    public static com.lulobank.credits.sdk.dto.loandetails.LoanDetail getCreditProductsFromGetLoanResponse(GetLoanResponse getLoanResponse, CreditsV3Entity creditsV3Entity) {

        FlexibilityLoanServices flexibilityLoanServices = new FlexibilityLoanServices(getLoanResponse);
        com.lulobank.credits.sdk.dto.loandetails.LoanDetail loanDetail = new com.lulobank.credits.sdk.dto.loandetails.LoanDetail();
        loanDetail.setIdCredit(creditsV3Entity.getIdCredit().toString());
        loanDetail.setProductType(getLoanResponse.getProductTypeKey());
        loanDetail.setBalance(getLoanResponse.getBalance().getAmount());
        loanDetail.setState(getLoanResponse.getAccountState());
        loanDetail.setInstallments(getLoanResponse.getPaymentPlanItemApiList().size());
        loanDetail.setPaidInstallments(flexibilityLoanServices.countPaidInstallments());
        loanDetail.setNextInstallment(flexibilityLoanServices.getNextInstallment());
        loanDetail.setInterestRate(getInterestRate(creditsV3Entity));
        loanDetail.setIdLoan(creditsV3Entity.getIdLoanAccountMambu());
        return loanDetail;

    }


    public static List<Payment> getPaymentListFromGetLoanMovementsResponse(GetLoanMovementsResponse getLoanMovementsResponse) {
        return getLoanMovementsResponse.getLoanMovementList().stream().limit(MAX_NUMBER_OF_LOAN_MOVEMENTS)
                .filter(x -> EnumUtils.isValidEnum(FlexibilityLoanMovementsPaidStatesEnum.class, x.getTransactionType()))
                .map(FlexibilityMapper::getPaymentFromLoanMovement)
                .collect(Collectors.toList());
    }

    private static Payment getPaymentFromLoanMovement(GetLoanMovementsResponse.LoanMovement loanMovement) {
        Payment payment = new Payment();
        payment.setTotalDue(loanMovement.getAmount());
        payment.setState(loanMovement.getTransactionType());
        Optional.ofNullable(loanMovement.getCreationDate()).ifPresent(date -> payment.setDue(date.toLocalDate()));
        com.lulobank.credits.sdk.dto.clientloandetail.PaymentDetail paymentDetail =
                new com.lulobank.credits.sdk.dto.clientloandetail.PaymentDetail();
        Optional.ofNullable(loanMovement.getAmounts()).ifPresent(obj -> {
                    paymentDetail.setInsuranceCost(obj.getFeesAmount());
                    paymentDetail.setCapitalPayment(obj.getPrincipalAmount());
                    paymentDetail.setRatePayment(obj.getInterestAmount());
                }
        );
        payment.setDetail(paymentDetail);
        return payment;
    }

    public static GetLoanMovementsRequest getLoanMovementsRequest(CreditsEntity creditsEntity, Integer limit, Integer offset) {
        return GetLoanMovementsRequestBuilder.
                getLoanMovementsRequest()
                .withClientId(creditsEntity.getIdClient())
                .withLoanNumber(creditsEntity.getIdLoanAccountMambu())
                .withLimit(limit)
                .withOffset(offset)
                .build();
    }

    public static GetLoanMovementsRequest getLoanMovementsRequest(CreditsEntity creditsEntity) {

        return GetLoanMovementsRequestBuilder.getLoanMovementsRequest()
                .withClientId(creditsEntity.getIdClientMambu())
                .withLoanNumber(creditsEntity.getIdLoanAccountMambu())
                .build();
    }
}
