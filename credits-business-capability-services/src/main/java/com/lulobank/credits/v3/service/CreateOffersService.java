package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import io.vavr.control.Option;
import lombok.CustomLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@CustomLog
public class CreateOffersService {

    private final SimulateByFormulaService simulateByFormulaService;
    private final SimulateService simulateService;
    private final Map<OffersTypeV3, BiFunction<OffersTypeV3, OfferInformationRequest, Option<OfferEntityV3>>> typeOffersToCalculate;

    public CreateOffersService(SimulateByFormulaService simulateByFormulaService, SimulateService simulateService) {
        this.simulateByFormulaService = simulateByFormulaService;
        this.simulateService = simulateService;
        typeOffersToCalculate = new HashMap<>();
        typeOffersToCalculate.put(OffersTypeV3.COMFORTABLE_LOAN, createOfferByLoanProvider());
        typeOffersToCalculate.put(OffersTypeV3.FAST_LOAN, createOfferByLoanProvider());
        typeOffersToCalculate.put(OffersTypeV3.FLEXIBLE_LOAN, createOfferFlexible());
    }

    public List<OfferEntityV3> calculate(OfferInformationRequest offerInformationRequest) {
        return typeOffersToCalculate.entrySet().stream()
                .map(pos -> getOfferByType(offerInformationRequest, pos))
                .filter(Objects::nonNull)
                .peek(offerEntityV3 -> log.info("Offer {} , created to idClient {} ", offerEntityV3.getType(), offerInformationRequest.getIdClient()))
                .collect(Collectors.toList());
    }

    private OfferEntityV3 getOfferByType(OfferInformationRequest offerInformationRequest, Map.Entry<OffersTypeV3, BiFunction<OffersTypeV3, OfferInformationRequest, Option<OfferEntityV3>>> pos) {
        return pos.getValue().apply(pos.getKey(), offerInformationRequest)
                .fold(() -> null, e -> e);
    }

    private BiFunction<OffersTypeV3, OfferInformationRequest, Option<OfferEntityV3>> createOfferFlexible() {
        return simulateByFormulaService::build;
    }

    private BiFunction<OffersTypeV3, OfferInformationRequest, Option<OfferEntityV3>> createOfferByLoanProvider() {
        return simulateService::createOffer;
    }

}
