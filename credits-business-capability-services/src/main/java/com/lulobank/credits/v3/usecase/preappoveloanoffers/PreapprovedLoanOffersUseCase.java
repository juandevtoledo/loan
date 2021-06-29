package com.lulobank.credits.v3.usecase.preappoveloanoffers;

import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.RiskResult;
import com.lulobank.credits.v3.dto.Schedule;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.service.SimulateByRiskResponse;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.command.GetOffersByIdClient;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.dto.OfferedResponse;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.mapper.OfferResponseMapper;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.lulobank.credits.v3.dto.CreditType.PREAPPROVED;

@CustomLog
@Getter
@AllArgsConstructor
public class PreapprovedLoanOffersUseCase implements UseCase<GetOffersByIdClient, Either<UseCaseResponseError, OfferedResponse>> {

    private final CreditsV3Repository creditsV3Repository;
    private final CoreBankingService coreBankingService;
    private final SimulateByRiskResponse simulateByRiskResponse;


    @Override
    public Either<UseCaseResponseError, OfferedResponse> execute(GetOffersByIdClient command) {
        return creditsV3Repository.findByIdClient(command.getIdClient())
                .filter(this::isPreapprovedLoan)
                .toEither(CreditsError::databaseError)
                .map(CreditsV3Entity::getInitialOffer)
                .flatMap(initialOfferV3 -> setFlexibleOffer(command, initialOfferV3))
                .flatMap(initialOfferV3 -> updateEntity(command, initialOfferV3))
                .map(OfferResponseMapper.INSTANCE::offeredResponseTo)
                .mapLeft(Function.identity());

    }


    private Either<CreditsError, InitialOfferV3> setFlexibleOffer(GetOffersByIdClient command, InitialOfferV3 initialOfferV3) {
        return findFirstRiskResult(initialOfferV3.getResults())
                .map(RiskResult::getSchedule)
                .flatMap(schedules -> simulateOfferWithInsurance(command, schedules))
                .flatMap(offerEntityV3 -> setNewValuesInInitialsOffers(command, initialOfferV3, offerEntityV3));
    }

    private Either<CreditsError, OfferEntityV3> simulateOfferWithInsurance(GetOffersByIdClient command, List<Schedule> schedules) {
        return coreBankingService.getInsuranceFee()
                .mapLeft(CreditsError::toCreditError)
                .flatMap(feeInsurance ->
                        simulateByRiskResponse.build(feeInsurance, command.getClientLoanRequestedAmount(), schedules)
                                .toEither(CreditsError.generateOfferError())
                );
    }

    private Either<CreditsError, InitialOfferV3> setNewValuesInInitialsOffers(GetOffersByIdClient command, InitialOfferV3 initialOfferV3, OfferEntityV3 offerEntityV3) {
        return findFirstRiskResult(initialOfferV3.getResults())
                .map(riskResult -> {
                    initialOfferV3.setGenerateDate(LocalDateTime.now());
                    initialOfferV3.setAmount(command.getClientLoanRequestedAmount().doubleValue());
                    initialOfferV3.setOfferEntities(Collections.singletonList(offerEntityV3));
                    initialOfferV3.setMaxAmount(riskResult.getMaxTotalAmount().doubleValue());
                    initialOfferV3.setMaxAmountInstallment(riskResult.getMaxAmountInstallment().doubleValue());
                    return initialOfferV3;
                });
    }

    private Either<CreditsError, CreditsV3Entity> updateEntity(GetOffersByIdClient command, InitialOfferV3 initialOfferV3) {
        return creditsV3Repository.findByIdClient(command.getIdClient())
                .filter(this::isPreapprovedLoan)
                .toTry()
                .map(creditsV3Entity -> {
                    creditsV3Entity.setInitialOffer(initialOfferV3);
                    return creditsV3Entity;
                })
                .flatMap(creditsV3Repository::save)
                .toEither(CreditsError.persistError());
    }

    private Either<CreditsError, RiskResult> findFirstRiskResult(List<RiskResult> riskResults) {
        return Option.ofOptional(riskResults.stream().findFirst())
                .toEither(CreditsError::databaseError);
    }

    private boolean isPreapprovedLoan(CreditsV3Entity creditsV3Entity) {
        return PREAPPROVED.equals(creditsV3Entity.getCreditType())
                && Objects.nonNull(creditsV3Entity.getInitialOffer())
                && Objects.nonNull(creditsV3Entity.getInitialOffer().getResults())
                && Objects.isNull(creditsV3Entity.getLoanStatus());
    }
}
