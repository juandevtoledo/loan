package com.lulobank.credits.services.features.clientproductoffer;

import com.lulobank.credits.sdk.dto.clientproduct.offer.FlexibleLoanSimulationInstallments;
import com.lulobank.credits.sdk.dto.clientproduct.offer.Offer;
import com.lulobank.credits.sdk.dto.clientproduct.offer.OfferFlexible;
import com.lulobank.credits.services.features.initialoffer.OffersTypeEnum;
import com.lulobank.credits.services.outboundadapters.model.FlexibleLoan;
import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import com.lulobank.credits.v3.util.RoundNumber;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FunctionsProductOffer {

    private FunctionsProductOffer() {
    }

    static Predicate<String> offerTypeisFlexibleLoan = offerType -> OffersTypeEnum.FLEXIBLE_LOAN.name().equals(offerType);

    static Function<List<OfferEntity>, List<com.lulobank.credits.sdk.dto.clientproduct.offer.Offer>> getDtoOffersFromEntityOffers = entityOffers -> {
        List<com.lulobank.credits.sdk.dto.clientproduct.offer.Offer> dtoOffers = new ArrayList<>();
        entityOffers.forEach(entityOffer -> {
            if (offerTypeisFlexibleLoan.test(entityOffer.getType())) {
                OfferFlexible offerFlexible = getDtoFlexibleOfferFromEntityFlexible(entityOffer);
                dtoOffers.add(offerFlexible);
            } else {
                com.lulobank.credits.sdk.dto.clientproduct.offer.Offer simpleOffer = getDtoSimpleOfferFromEntitySimpleOffer(entityOffer);
                dtoOffers.add(simpleOffer);
            }
        });
        return dtoOffers;
    };


    @NotNull
    private static OfferFlexible getDtoFlexibleOfferFromEntityFlexible(OfferEntity entityOffer) {
        OfferFlexible offerFlexible = new OfferFlexible();
        offerFlexible.setAmount(entityOffer.getAmount());
        offerFlexible.setAmountInstallment(entityOffer.getAmountInstallment());
        offerFlexible.setIdOffer(entityOffer.getIdOffer());
        offerFlexible.setInsuranceCost(entityOffer.getFeeInsurance());
        offerFlexible.setInterestRate(entityOffer.getInterestRate());
        offerFlexible.setType(entityOffer.getType());
        offerFlexible.setName(entityOffer.getName());
        offerFlexible.setSimulateInstallment(getDtoFlexibleInstallmentsFromEntityFlexibleInstallments(entityOffer));
        offerFlexible.setMonthlyNominalRate(RoundNumber.defaultScale(entityOffer.getMonthlyNominalRate()));
        return offerFlexible;
    }

    private static List<FlexibleLoanSimulationInstallments> getDtoFlexibleInstallmentsFromEntityFlexibleInstallments(OfferEntity entityOffer) {

        Optional<List<FlexibleLoan>> flexibleLoans = Optional.ofNullable(entityOffer.getFlexibleLoans());
        return flexibleLoans.orElse(new ArrayList<>()).stream()
                .map(flexibleLoan -> new FlexibleLoanSimulationInstallments(flexibleLoan.getInstallment(), flexibleLoan.getAmount()))
                .collect(Collectors.toList());

    }

    @NotNull
    private static com.lulobank.credits.sdk.dto.clientproduct.offer.Offer getDtoSimpleOfferFromEntitySimpleOffer(OfferEntity entityOffer) {
        return  Offer.builder()
                .amount(entityOffer.getAmount())
                .amountInstallment(entityOffer.getAmountInstallment())
                .idOffer(entityOffer.getIdOffer())
                .installments(entityOffer.getInstallments())
                .interestRate(entityOffer.getInterestRate())
                .insuranceCost(entityOffer.getFeeInsurance())
                .type(entityOffer.getType())
                .name(entityOffer.getName())
                .monthlyNominalRate(entityOffer.getMonthlyNominalRate())
                .build();
    }
}
