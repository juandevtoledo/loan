package com.lulobank.credits.services;

import com.lulobank.credits.services.features.initialoffer.OffersTypeEnum;
import com.lulobank.credits.services.features.initialoffer.RiskModelResponse;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.FlexibleLoan;
import com.lulobank.credits.services.outboundadapters.model.InitialOffer;
import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import com.lulobank.credits.v3.dto.PaymentV3;
import io.vavr.control.Option;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.lulobank.credits.services.Constant.FEES_DUE;
import static com.lulobank.credits.services.Constant.INTEREST_DUE;
import static com.lulobank.credits.services.Constant.MONTHLY_NOMINAL_RATE;
import static com.lulobank.credits.services.Constant.TOTAL_DUE;
import static java.util.UUID.fromString;

public class Sample {
    private Sample() {
    }

    public static final CreditsEntity creditsEntityBuilder(String idCredit, String idClient, InitialOffer initialOffer) {
        CreditsEntity creditsEntity = new CreditsEntity();
        creditsEntity.setIdClient(idClient);
        creditsEntity.setIdCredit(fromString(idCredit));
        creditsEntity.setInitialOffer(initialOffer);
        return creditsEntity;
    }

    public static final InitialOffer initialOffersBuilder(RiskModelResponse responseEnum, Double amount, LocalDateTime generate, List<OfferEntity> offerEntities) {
        InitialOffer initialOffer = new InitialOffer();
        initialOffer.setTypeOffer(responseEnum.name());
        initialOffer.setOfferEntities(offerEntities);
        initialOffer.setAmount(amount);
        initialOffer.setGenerateDate(generate);
        return initialOffer;
    }

    public static final OfferEntity offerEntityBuilder(String idOffer, OffersTypeEnum offersTypeEnum, Double amount, Double amountInstallment,
                                                       Double feeInsurance, Float interestRate, List<FlexibleLoan> flexibleLoans) {

        OfferEntity offerEntity = new OfferEntity();
        offerEntity.setIdOffer(idOffer);
        offerEntity.setType(offersTypeEnum.name());
        offerEntity.setInstallments(offersTypeEnum.getInstallment());
        offerEntity.setName(offersTypeEnum.getDescription());
        offerEntity.setFeeInsurance(feeInsurance);
        offerEntity.setInterestRate(interestRate);
        offerEntity.setAmount(amount);
        offerEntity.setAmountInstallment(amountInstallment);
        offerEntity.setMonthlyNominalRate(BigDecimal.valueOf(MONTHLY_NOMINAL_RATE));
        Option.of(flexibleLoans).peek(list -> offerEntity.setFlexibleLoans(list));
        return offerEntity;
    }

    public static final FlexibleLoan flexibleLoanBuilder(Integer installment, Double amount) {
        FlexibleLoan flexibleLoan = new FlexibleLoan();
        flexibleLoan.setInstallment(installment);
        flexibleLoan.setAmount(BigDecimal.valueOf(amount));
        return flexibleLoan;
    }

    public static final PaymentV3 paymentV3Builder() {
        PaymentV3 paymentV3 = new PaymentV3();
        paymentV3.setDueDate(LocalDate.now());
        paymentV3.setInstallment(1);
        paymentV3.setTotalDue(TOTAL_DUE);
        paymentV3.setFeesDue(FEES_DUE);
        paymentV3.setInterestDue(INTEREST_DUE);
        return paymentV3;
    }
}
