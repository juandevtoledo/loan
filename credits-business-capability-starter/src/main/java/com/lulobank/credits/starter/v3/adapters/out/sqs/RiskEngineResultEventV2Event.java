package com.lulobank.credits.starter.v3.adapters.out.sqs;

import com.lulobank.credits.services.events.CreatePreApprovedOfferMessage;
import com.lulobank.credits.v3.dto.RiskResult;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Option;

import java.util.List;

public class RiskEngineResultEventV2Event extends SqsV3Integration<CreditsV3Entity, CreatePreApprovedOfferMessage> {

    public RiskEngineResultEventV2Event(String endpoint) {
        super(endpoint);
    }

    @Override
    public Event<CreatePreApprovedOfferMessage> map(CreditsV3Entity event) {
        CreatePreApprovedOfferMessage createPreapprovedOfferMessage = new CreatePreApprovedOfferMessage();
        createPreapprovedOfferMessage.setIdClient(event.getIdClient());
        List<RiskResult> results = event.getInitialOffer().getResults();
        Option.ofOptional(results.stream().findFirst())
                .peek(e -> createPreapprovedOfferMessage.setMaxTotalAmount(e.getMaxTotalAmount()));
        return EventFactory.ofDefaults(createPreapprovedOfferMessage).build();
    }
}
