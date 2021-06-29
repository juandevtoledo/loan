package com.lulobank.credits.v3.port.in.rescheduledloan;

import com.lulobank.credits.v3.dto.ModifiedLoan;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.util.UseCase;
import io.vavr.control.Try;
import lombok.CustomLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static com.lulobank.credits.services.utils.InterestUtil.getAnnualEffectiveRateFromAnnualNominalRate;
import static com.lulobank.credits.services.utils.InterestUtil.getMonthlyNominalRateFromAnnualNominalRate;

@CustomLog
public class RescheduledLoanUseCase implements UseCase<RescheduledLoanMessage, Try<Void>> {

    public static final String CREDIT_NOT_FOUND_MESSAGE = "Credit not found, idLoanAccountMambu : %s ";

    private final CreditsV3Repository creditsV3Repository;

    public RescheduledLoanUseCase(CreditsV3Repository creditsV3Repository) {
        this.creditsV3Repository = creditsV3Repository;
    }

    @Override
    public Try<Void> execute(RescheduledLoanMessage rescheduledLoanMessage) {
        return Try.run(() -> findCreditByIdLoan(rescheduledLoanMessage.getOriginalLoanId())
                .map(creditsV3Entity -> addModifiedLoanToHistory(creditsV3Entity,
                        rescheduledLoanMessage.getModificationType()))
                .map(creditsV3Entity -> updateLoanConditions(creditsV3Entity, rescheduledLoanMessage))
                .flatMap(creditsV3Repository::save)
                .onFailure(error -> handleRescheduledError(rescheduledLoanMessage, error))
        );
    }

    private CreditsV3Entity addModifiedLoanToHistory(CreditsV3Entity entity, String modificationType) {
        ModifiedLoan modifiedLoan = new ModifiedLoan();
        modifiedLoan.setIdLoanAccountMambu(entity.getIdLoanAccountMambu());
        modifiedLoan.setInterestRate(entity.getAcceptOffer().getInterestRate());
        modifiedLoan.setAnnualNominalRate(entity.getAcceptOffer().getAnnualNominalRate());
        modifiedLoan.setMonthlyNominalRate(entity.getAcceptOffer().getMonthlyNominalRate());
        modifiedLoan.setAcceptDate(entity.getAcceptDate());
        modifiedLoan.setModificationType(modificationType);
        modifiedLoan.setInstallments(entity.getAcceptOffer().getInstallments());
        modifiedLoan.setAmount(BigDecimal.valueOf(entity.getAcceptOffer().getAmount()));

        entity.getModifiedHistory().add(modifiedLoan);

        return entity;
    }

    private CreditsV3Entity updateLoanConditions(CreditsV3Entity entity, RescheduledLoanMessage rescheduledLoanMessage) {
        entity.setIdLoanAccountMambu(rescheduledLoanMessage.getRescheduledLoan().getId());
        entity.setAcceptDate(LocalDateTime.parse(rescheduledLoanMessage.getRescheduledLoan().getCreationDate()));
        OfferEntityV3 acceptOffer = entity.getAcceptOffer();
        BigDecimal annualNominalRate = rescheduledLoanMessage.getRescheduledLoan().getInterestRate();
        BigDecimal amount = rescheduledLoanMessage.getRescheduledLoan().getLoanAmount().getAmount();
        acceptOffer.setInterestRate(getAnnualEffectiveRateFromAnnualNominalRate(annualNominalRate));
        acceptOffer.setAnnualNominalRate(annualNominalRate);
        acceptOffer.setMonthlyNominalRate(getMonthlyNominalRateFromAnnualNominalRate(annualNominalRate));
        acceptOffer.setInstallments(rescheduledLoanMessage.getRescheduledLoan().getInstallments());
        acceptOffer.setAmount(amount.setScale(2, RoundingMode.HALF_UP).doubleValue());
        return entity;
    }

    private void handleRescheduledError(RescheduledLoanMessage rescheduledLoanMessage, Throwable error) {
        log.error("Error rescheduling the loan, idClientMambu : {}, " +
                        "IdLoanAccountMambu: {}, error : {} ",
                rescheduledLoanMessage.getRescheduledLoan().getClientId(),
                rescheduledLoanMessage.getOriginalLoanId(), error.getMessage());
    }

    private Try<CreditsV3Entity> findCreditByIdLoan(String originalLoanId) {
        return creditsV3Repository
                .findByIdLoanAccountMambu(originalLoanId)
                .onEmpty(() -> log.error(String.format(CREDIT_NOT_FOUND_MESSAGE, originalLoanId)))
                .toTry();
    }
}
