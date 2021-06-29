package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResultEventV2Message;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResultEventV2UseCase;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;

public class RiskEngineResultEventV2Handler implements EventHandler<RiskEngineResultEventV2Message> {

    private final RiskEngineResultEventV2UseCase riskEngineResultEventv2UseCase;

    public RiskEngineResultEventV2Handler(RiskEngineResultEventV2UseCase riskEngineResultEventv2UseCase) {
        this.riskEngineResultEventv2UseCase = riskEngineResultEventv2UseCase;
    }

    @Override
    public Try<Void> execute(RiskEngineResultEventV2Message payload) { return riskEngineResultEventv2UseCase.execute(payload); }

    @Override
    public Class<RiskEngineResultEventV2Message> eventClass() { return RiskEngineResultEventV2Message.class; }
}
