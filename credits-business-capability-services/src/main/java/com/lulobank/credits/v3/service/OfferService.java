package com.lulobank.credits.v3.service;

import java.util.List;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;

import io.vavr.control.Option;
import lombok.CustomLog;

@CustomLog
public class OfferService {


    public Option<OfferEntityV3> getOffer(CreditsV3Entity creditsV3Entities, AcceptOffer offerV3) {

        return searchOfferEntity(creditsV3Entities.getInitialOffer()
                .getOfferEntities(), offerV3.getSelectedCredit().getIdOffer())
                .map(mapIfFlexibleLoan(offerV3))
                .map(offerEntityV3 -> offerEntityV3)
                .onEmpty(() -> log.error("Offer not found!"));
    }

    @NotNull
    private UnaryOperator<OfferEntityV3> mapIfFlexibleLoan(AcceptOffer offerV3) {
        return offerEntityV3 -> {
            if (OffersTypeV3.FLEXIBLE_LOAN.name().equals(offerEntityV3.getType())) {
                offerEntityV3.setInstallments(offerV3.getSelectedCredit().getInstallments());
                offerEntityV3.setAmountInstallment(offerV3.getSelectedCredit().getAmountInstallment());
            }
            return offerEntityV3;
        };
    }

    private Option<OfferEntityV3> searchOfferEntity(List<OfferEntityV3> list, String idOffer) {
        return Option.ofOptional(list.stream()
                .filter(offerEntityV3 -> offerEntityV3.getIdOffer().equals(idOffer))
                .findFirst());
    }


}
