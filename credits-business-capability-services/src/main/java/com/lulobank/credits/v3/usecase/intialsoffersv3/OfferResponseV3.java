package com.lulobank.credits.v3.usecase.intialsoffersv3;

import com.lulobank.credits.v3.dto.InitialOfferV3;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.lulobank.credits.v3.service.OffersTypeV3.COMFORTABLE_LOAN;
import static com.lulobank.credits.v3.service.OffersTypeV3.FAST_LOAN;

@Getter
public enum OfferResponseV3 {

    KO("DeniedOffer") {
        @Override
        public boolean predicate(InitialOfferV3 initialOfferV3) {
            return initialOfferV3.getOfferEntities()
                    .stream()
                    .noneMatch(offer -> FAST_LOAN.name().equals(offer.getType()) || COMFORTABLE_LOAN.name().equals(offer.getType()));
        }
    },
    CO("CounterOffer") {
        @Override
        public boolean predicate(InitialOfferV3 initialOfferV3) {
            return BigDecimal.valueOf(initialOfferV3.getRiskEngineAnalysis().getAmount())
                    .compareTo(initialOfferV3.getClientLoanRequestedAmount()) < 0;
        }
    },
    OK("TotalOffer") {
        @Override
        public boolean predicate(InitialOfferV3 initialOfferV3) {
            return BigDecimal.valueOf(initialOfferV3.getRiskEngineAnalysis().getAmount())
                    .compareTo(initialOfferV3.getClientLoanRequestedAmount()) >= 0;
        }
    };

    public abstract boolean predicate(InitialOfferV3 initialOfferV3);

    private final String description;

    OfferResponseV3(String description) {
        this.description = description;
    }

    public static OfferResponseV3 get(InitialOfferV3 initialOfferV3) {
        return Arrays.stream(OfferResponseV3.values())
                .filter(offerResponseV3 -> offerResponseV3.predicate(initialOfferV3))
                .findFirst()
                .orElse(KO);
    }


}
