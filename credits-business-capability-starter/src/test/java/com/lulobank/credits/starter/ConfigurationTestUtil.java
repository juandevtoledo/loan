package com.lulobank.credits.starter;


import com.lulobank.credits.services.features.initialoffer.OffersTypeEnum;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.InitialOffer;
import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import com.lulobank.credits.services.outboundadapters.model.RiskEngineAnalysis;
import com.lulobank.credits.services.outboundadapters.riskengine.model.RiskEngineOffer;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static com.lulobank.credits.starter.utils.Constants.AMOUNT;
import static com.lulobank.credits.starter.utils.Constants.AMOUNT_INSTALLMENT;
import static com.lulobank.credits.starter.utils.Constants.ID_OFFER;
import static com.lulobank.credits.starter.utils.Constants.INTEREST_RATE;
import static java.util.UUID.fromString;

public class ConfigurationTestUtil {

    private ConfigurationTestUtil() {
    }

    protected static RiskEngineOffer createOffer(Double amount, Integer installment, String type, Double amountInstallment,
                                                 Float interestRate) {

        RiskEngineOffer offer = new RiskEngineOffer();
        offer.setAmount(amount);
        offer.setInstallments(installment);
        offer.setType(type);
        offer.setMaxAmountInstallment(amountInstallment);
        offer.setInterestRate(interestRate);
        return offer;
    }

    protected static OfferEntity createOfferModel(String idOffer, Double amount,
                                                  Integer installment, String type, Double amountInstallment,
                                                  Float interestRate) {
        OfferEntity offerEntity = new OfferEntity();
        offerEntity.setIdOffer(idOffer);
        offerEntity.setAmount(amount);
        offerEntity.setInstallments(installment);
        offerEntity.setType(type);
        offerEntity.setAmount(amountInstallment);
        offerEntity.setInterestRate(interestRate);
        return offerEntity;
    }

    protected static CreditsEntity createCreditsForTest(String idClient, String idCredit, Double maxAmount, Double minAmount,
                                                        Float interestRate) {

        CreditsEntity creditsEntity = new CreditsEntity();
        creditsEntity.setIdClient(idClient);
        creditsEntity.setIdCredit(fromString(idCredit));
        InitialOffer initialOffer = new InitialOffer();
        initialOffer.setMaxAmount(maxAmount);
        initialOffer.setMinAmount(minAmount);
        initialOffer.setInterestRate(interestRate);
        creditsEntity.setInitialOffer(initialOffer);

        return creditsEntity;
    }

    protected static CreditsEntity buildCreditsEntity(String idClient, String idClientMambu, String idCredit) {
        CreditsEntity creditsEntity = createCreditsForTest(idClient, idCredit, 3000000d, 500000d, 0f);
        creditsEntity.setAcceptDate(LocalDateTime.now());
        creditsEntity.setIdClientMambu(idClientMambu);
        RiskEngineAnalysis riskEngineAnalysis = new RiskEngineAnalysis();
        riskEngineAnalysis.setInterestRate(0f);
        creditsEntity.getInitialOffer().setRiskEngineAnalysis(riskEngineAnalysis);
        OfferEntity offerModel = createOfferModel(ID_OFFER, AMOUNT, OffersTypeEnum.FAST_LOAN.getInstallment(),
                OffersTypeEnum.FAST_LOAN.name(), AMOUNT_INSTALLMENT, INTEREST_RATE);
        creditsEntity.getInitialOffer().setOfferEntities(newArrayList(offerModel));
        creditsEntity.getInitialOffer().setGenerateDate(LocalDateTime.now());
        creditsEntity.setLoanConditionsList(new ArrayList<>());
        return creditsEntity;
    }

}
