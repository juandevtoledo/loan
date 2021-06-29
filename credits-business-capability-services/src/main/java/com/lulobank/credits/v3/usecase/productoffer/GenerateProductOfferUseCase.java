package com.lulobank.credits.v3.usecase.productoffer;

import com.lulobank.credits.v3.dto.CreditType;
import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.LoanRequestedV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.RiskEngineAnalysisV3;
import com.lulobank.credits.v3.port.in.productoffer.GenerateProductOfferPort;
import com.lulobank.credits.v3.port.in.productoffer.dto.ProductOffer;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.service.OffersTypeV3;
import com.lulobank.credits.v3.service.SimulateByFormulaService;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.usecase.productoffer.command.GenerateOfferRequest;
import com.lulobank.credits.v3.usecase.productoffer.mapper.OfferInformationRequestMapper;
import com.lulobank.credits.v3.usecase.productoffer.mapper.ProductOfferMapper;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@CustomLog
@RequiredArgsConstructor
public class GenerateProductOfferUseCase implements GenerateProductOfferPort {

    private final CreditsV3Repository creditsV3Repository;
    private final SimulateByFormulaService simulateByFormulaService;
    private final CoreBankingService coreBankingService;

    @Override
    public Either<UseCaseResponseError, ProductOffer> execute(GenerateOfferRequest command) {
        return creditsV3Repository.findByIdProductOffer(command.getIdProductOffer())
                .filter(getCreditsV3EntityPredicate(command))
                .toEither(CreditsError::databaseError)
                .flatMap(entity -> generateOffer(entity, command))
                .peekLeft(err -> log.error("Error trying to generate offer:{}", command.getIdClient()))
                .mapLeft(Function.identity());
    }

    private Predicate<CreditsV3Entity> getCreditsV3EntityPredicate(GenerateOfferRequest command) {
        return entity -> CreditType.PREAPPROVED.equals(entity.getCreditType())
                && entity.getInitialOffer().getRiskEngineAnalysis().getAmount().compareTo(command.getAmount()) >= 0;
    }

    private Either<CreditsError, ProductOffer> generateOffer(CreditsV3Entity entity, GenerateOfferRequest command) {
        return coreBankingService.getInsuranceFee()
                .mapLeft(CreditsError::toCreditError)
                .peekLeft(err -> log.error("Error trying to get insurance fee from coreBanking:{}", command.getIdClient()))
                .map(insurance -> OfferInformationRequestMapper.INSTANCE.toOfferInformationRequest(command, entity, insurance))
                .flatMap(req -> simulateOffer(req, entity))
                .map(offer -> updateEntity(entity, offer, command))
                .flatMap(this::saveEntity)
                .flatMap(this::generateResponse);
    }

    private Either<CreditsError, OfferEntityV3> simulateOffer(OfferInformationRequest offerReq, CreditsV3Entity entity) {
        return simulateByFormulaService.build(OffersTypeV3.FLEXIBLE_LOAN, offerReq)
                .map(offer -> mapOffer(offer, entity))
                .toEither(CreditsError.generateOfferError())
                .peekLeft(err -> log.error("Error trying to simulate offer:{} - {}", offerReq.getIdClient(), err.getProviderCode()));
    }

    private OfferEntityV3 mapOffer(OfferEntityV3 offer, CreditsV3Entity entity) {
        offer.setFlexibleLoans(filterFlexibleLoans(offer, entity.getInitialOffer().getRiskEngineAnalysis()));
        return offer;
    }

    private List<FlexibleLoanV3> filterFlexibleLoans(OfferEntityV3 offer, RiskEngineAnalysisV3 riskEngineAnalysis) {
        return Stream.ofAll(offer.getFlexibleLoans())
                .takeWhile(loan -> loan.getAmount().compareTo(BigDecimal.valueOf(riskEngineAnalysis.getMaxAmountInstallment())) < 0)
                .toJavaList();
    }

    private CreditsV3Entity updateEntity(CreditsV3Entity entity, OfferEntityV3 offer, GenerateOfferRequest command) {
        return Option.of(offer)
                .map(Collections::singletonList)
                .map(offerList -> {
                    entity.getInitialOffer().setOfferEntities(offerList);
                    entity.setLoanRequested(new LoanRequestedV3(command.getAmount(), command.getLoanPurpose()));
                    entity.getInitialOffer().setAmount(command.getAmount());
                    entity.getInitialOffer().setGenerateDate(LocalDateTime.now());
                    return entity;
                }).getOrElse(entity);
    }

    private Either<CreditsError, CreditsV3Entity> saveEntity(CreditsV3Entity entity) {
        return creditsV3Repository.save(entity)
                .onFailure(error -> log.error("Error saving entity into database: {}", error.getMessage()))
                .toEither(CreditsError.databaseError());
    }

    private Either<CreditsError, ProductOffer> generateResponse(CreditsV3Entity entity) {
        return Option.ofOptional(entity.getInitialOffer().getOfferEntities().stream().findFirst())
                .toEither(CreditsError.generateOfferError())
                .map(offer -> ProductOfferMapper.INSTANCE.toOfferInformationRequest(offer, entity));
    }
}
