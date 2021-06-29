package com.lulobank.credits.services.features.clientloandetail;

import com.lulobank.credits.sdk.dto.clientloandetail.LoanDetail;
import com.lulobank.credits.sdk.dto.clientloandetail.LoanStateHomologate;
import com.lulobank.credits.sdk.dto.clientloandetail.Payment;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import com.lulobank.credits.v3.util.RoundNumber;
import flexibility.client.models.response.GetLoanResponse;
import io.vavr.control.Option;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityLoanMovementsPaidStatesEnum.PAID;
import static java.math.BigDecimal.ZERO;

@Mapper(imports = {LoanStateHomologate.class, RoundNumber.class})
public interface ClientLoanDetailMapper {

    ClientLoanDetailMapper INSTANCE = Mappers.getMapper(ClientLoanDetailMapper.class);

    @Mapping(target = "amount", source = "creditsEntity.acceptOffer.amount")
    @Mapping(target = "balance", source = "getLoanResponse.totalBalance.amount")
    @Mapping(target = "state", expression = "java(LoanStateHomologate.getStatusHomologate(accoutState))")
    @Mapping(target = "installments", source = "creditsEntity.acceptOffer.installments")
    @Mapping(target = "monthlyInstallment", source = "creditsEntity.acceptOffer.amountInstallment")
    @Mapping(target = "interestRate", source = "creditsEntity.acceptOffer.interestRate")
    @Mapping(target = "automaticDebit", source = "creditsEntity.automaticDebit")
    @Mapping(target = "createOn", source = "getLoanResponse.creationDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "idCreditCBS", source = "creditsEntity.idLoanAccountMambu")
    @Mapping(target = "idCredit", ignore = true)
    @Mapping(target = "paidInstallments", defaultValue = "0")
    @Mapping(target = "paidAmount", defaultValue = "0")
    @Mapping(target = "dueInstallments", defaultValue = "0")
    LoanDetail creditsEntityToLoanDetail(CreditsEntity creditsEntity, GetLoanResponse getLoanResponse, List<Payment> payments, String accoutState);

    @AfterMapping
    default void setIdCredit(CreditsEntity creditsEntity, @MappingTarget LoanDetail loanDetail) {

        if (Objects.nonNull(creditsEntity)) {
            loanDetail.setIdCredit(creditsEntity.getIdCredit().toString());
        }

    }

    @AfterMapping
    default void setPaidAmount(List<Payment> payments, @MappingTarget LoanDetail loanDetail) {
        if (Objects.nonNull(payments)) {
            loanDetail.setPaidAmount(payments.stream().mapToDouble(Payment::getTotalDue).sum());
        }
    }

    @AfterMapping
    default void setPaidInstallments(GetLoanResponse getLoanResponse, @MappingTarget LoanDetail loanDetail) {
        if (Objects.nonNull(getLoanResponse)) {
            List<GetLoanResponse.PaymentPlanItem> paymentsPaid = getLoanResponse.getPaymentPlanItemApiList().stream().filter(instalment -> PAID.name().equals(instalment.getState())).collect(Collectors.toList());
            loanDetail.setPaidInstallments(paymentsPaid.size());
            loanDetail.setDueInstallments(loanDetail.getInstallments() - loanDetail.getPaidInstallments());
        }
    }

    @AfterMapping
    default void setRoundRate(CreditsEntity creditsEntity, @MappingTarget LoanDetail loanDetail) {
        loanDetail.setMonthlyNominalRate(
                Option.of(creditsEntity)
                        .map(CreditsEntity::getAcceptOffer)
                        .map(OfferEntity::getMonthlyNominalRate)
                        .map(RoundNumber::defaultScale)
                        .getOrElse(ZERO)
        );
    }
}
