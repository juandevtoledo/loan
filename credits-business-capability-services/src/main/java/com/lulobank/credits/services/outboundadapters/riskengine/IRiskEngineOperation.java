package com.lulobank.credits.services.outboundadapters.riskengine;

import com.lulobank.credits.services.exceptions.RiskEngineException;
import com.lulobank.credits.services.outboundadapters.riskengine.model.RiskModelScore;
import org.springframework.http.ResponseEntity;

public interface IRiskEngineOperation {
    ResponseEntity<RiskModelScore> getRiskModelByIdCredit(String idCredit) throws RiskEngineException;
    ResponseEntity<RiskModelScore> getRiskModelByIdCreditDummy(String idCredit) throws RiskEngineException;
}
