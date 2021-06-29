package com.lulobank.credits.v3.usecase.intialsoffersv3;

import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.service.CreateOffersService;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.usecase.intialsoffersv3.command.GetOffersByClient;
import com.lulobank.credits.v3.usecase.intialsoffersv3.mapper.CreditsV3EntityMapper;
import com.lulobank.credits.v3.usecase.intialsoffersv3.mapper.InitialOfferV3Mapper;
import com.lulobank.credits.v3.usecase.intialsoffersv3.mapper.OfferInformationRequestMapper;
import com.lulobank.credits.v3.util.UseCase;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class InitialsOffersV3UseCase implements UseCase<GetOffersByClient, Try<CreditsV3Entity>> {

    private final CreditsV3Repository creditsV3Repository;
    private final CreateOffersService createOffersService;
    private final Double feeInsurance;

    @Override
    public Try<CreditsV3Entity> execute(GetOffersByClient command) {

        return creditsV3Repository.save(CreditsV3EntityMapper.INSTANCE.creditsV3EntityTo(buildInitialOfferV3(command), command))
                .onFailure(error -> log.error("Error creating InitialsOffers by idClient : {} , msg : {} ", command.getIdClient(), error.getMessage()));

    }

    private InitialOfferV3 buildInitialOfferV3(GetOffersByClient command) {
        OfferInformationRequest offerInformationRequest = OfferInformationRequestMapper.INSTANCE.offerInformationRequestTo(command, feeInsurance);
        InitialOfferV3 initialOfferV3 = InitialOfferV3Mapper.INSTANCE.initialOfferV3To(command);
        initialOfferV3.setOfferEntities(createOffersService.calculate(offerInformationRequest));
        initialOfferV3.setAmount(offerInformationRequest.getLoanAmount());
        initialOfferV3.setTypeOffer(OfferResponseV3.get(initialOfferV3).name());
        return initialOfferV3;
    }

}


