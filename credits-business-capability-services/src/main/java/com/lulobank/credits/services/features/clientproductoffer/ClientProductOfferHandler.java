package com.lulobank.credits.services.features.clientproductoffer;

import com.amazonaws.SdkClientException;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.credits.sdk.dto.clientproduct.offer.Offered;
import com.lulobank.credits.services.features.riskmodelscore.model.ClientProductOffer;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import com.lulobank.credits.services.outboundadapters.repository.CreditsRepository;
import com.lulobank.credits.services.utils.UtilValidators;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.lulobank.credits.services.features.clientproductoffer.FunctionsProductOffer.getDtoOffersFromEntityOffers;
import static com.lulobank.credits.services.utils.CreditsErrorResultEnum.OFFER_NOT_FOUND;
import static com.lulobank.credits.services.utils.DatesUtil.TIMESTAMP_FORMAT;
import static com.lulobank.credits.services.utils.DatesUtil.getLocalDateTimeByFormatter;
import static com.lulobank.credits.services.utils.HttpCodes.BAD_GATEWAY;
import static com.lulobank.credits.services.utils.HttpCodes.NOT_FOUND;
import static com.lulobank.credits.services.utils.LogMessages.INTERNAL_DYNAMODB_ERROR;
import static java.util.Objects.isNull;

@Slf4j
public class ClientProductOfferHandler implements Handler<Response<Offered>, ClientProductOffer> {
    private final CreditsRepository repository;

    public ClientProductOfferHandler(CreditsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Response<Offered> handle(ClientProductOffer clientProductOffer) {
        return Try.of(() -> getOfferedByClient(clientProductOffer))
                .recover(SdkClientException.class, this::handleDynamoDBException)
                .onFailure(e->log.error("Error in product Offer , msg {}  ",e.getMessage(),e))
                .get();
    }

    @NotNull
    private Response<Offered> handleDynamoDBException(SdkClientException ex) {
        log.error(INTERNAL_DYNAMODB_ERROR.getMessage(), ex.getMessage(), ex);
        return new Response<>(UtilValidators.getListValidations(INTERNAL_DYNAMODB_ERROR.name(),
                BAD_GATEWAY));
    }

    @NotNull
    private Response<Offered> getOfferedByClient(ClientProductOffer clientProductOffer) {
        return Option.ofOptional(getLastOfferByClient(clientProductOffer))
                .map(creditsEntity -> new Response<>(buildOfferForClient(creditsEntity)))
                .getOrElse(() -> new Response<>(UtilValidators.getListValidations(OFFER_NOT_FOUND.name(), NOT_FOUND)));
    }

    @NotNull
    private Optional<CreditsEntity> getLastOfferByClient(ClientProductOffer clientProductOffer) {
        return repository.findByidClient(clientProductOffer.getIdClient())
                .stream()
                .filter(creditsEntity -> isNull(creditsEntity.getIdLoanAccountMambu()))
                .filter(creditsEntity -> isNull(creditsEntity.getCreditType()))
                .sorted(comparingGenerateOffer().reversed())
                .findFirst();
    }

    private Comparator<CreditsEntity> comparingGenerateOffer() {
        return Comparator.comparing(creditsEntity -> creditsEntity.getInitialOffer().getGenerateDate());
    }

    private Offered buildOfferForClient(CreditsEntity creditsEntity) {
        Offered offered = new Offered();
        offered.setAmount(creditsEntity.getInitialOffer().getAmount());
        offered.setIdCredit(creditsEntity.getIdCredit().toString());
        offered.setRiskModelResponse(creditsEntity.getInitialOffer().getTypeOffer());
        Optional<List<OfferEntity>> offers = Optional.ofNullable(creditsEntity.getInitialOffer().getOfferEntities());
        offered.setOffers(getDtoOffersFromEntityOffers.apply(offers.orElse(new ArrayList<>())));
        offered.setCurrentDate(getLocalDateTimeByFormatter(LocalDateTime.now(), TIMESTAMP_FORMAT));
        return offered;
    }


}