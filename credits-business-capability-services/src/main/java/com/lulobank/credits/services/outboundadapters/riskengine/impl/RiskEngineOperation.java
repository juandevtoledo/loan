package com.lulobank.credits.services.outboundadapters.riskengine.impl;

import com.lulobank.credits.services.exceptions.RiskEngineException;
import com.lulobank.credits.services.outboundadapters.riskengine.IRiskEngineOperation;
import com.lulobank.credits.services.outboundadapters.riskengine.model.RiskEngineOffer;
import com.lulobank.credits.services.outboundadapters.riskengine.model.RiskEngineStatus;
import com.lulobank.credits.services.outboundadapters.riskengine.model.RiskModelScore;
import com.lulobank.credits.services.utils.CreditsErrorMessagesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RiskEngineOperation implements IRiskEngineOperation {

    private static final Logger log = LoggerFactory.getLogger(RiskEngineOperation.class);

    RiskModelApi riskModelApi;

    private static final Double AMOUNT_DUMMY = 3000000.0;
    private static final Float INTEREST_RATE_DUMMY = 16.5f;
    private static final Integer INSTALLMENTS_DUMMY = 12;
    private static final Double MAX_AMOUNT_INSTALLMENTS_DUMMY = 271298.6;
    private static final String TYPE_DUMMY = "dummy";

    public RiskEngineOperation(Retrofit retrofit) {
        this.riskModelApi = retrofit.create(RiskModelApi.class);
    }


    @Override
    public ResponseEntity<RiskModelScore> getRiskModelByIdCredit(String idCredit) throws RiskEngineException {
        Call<RiskModelScore> p = this.riskModelApi.getRiskModelByClientId(idCredit);
        try {
            log.info("Request Risk Engine  {idCredit} {}", idCredit);
            Response<RiskModelScore> response = p.execute();
            return getResponseEntityByRetrofitResponse(response);
        } catch (RiskEngineException | IOException e) {
            throw new RiskEngineException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @Override
    public ResponseEntity<RiskModelScore> getRiskModelByIdCreditDummy(String idCredit) {

        RiskModelScore riskModelScore = new RiskModelScore();
        List<RiskEngineOffer> riskEngineOfferList = new ArrayList<>();
        RiskEngineOffer riskEngineOffer = new RiskEngineOffer();
        riskEngineOffer.setType(TYPE_DUMMY);
        riskEngineOffer.setAmount(AMOUNT_DUMMY);
        riskEngineOffer.setInterestRate(INTEREST_RATE_DUMMY);
        riskEngineOffer.setInstallments(INSTALLMENTS_DUMMY);
        riskEngineOffer.setMaxAmountInstallment(MAX_AMOUNT_INSTALLMENTS_DUMMY);
        riskEngineOfferList.add(riskEngineOffer);
        riskModelScore.setStatus(RiskEngineStatus.RUNNING.name());
        riskModelScore.setRiskEngineOffer(riskEngineOfferList);
        return new ResponseEntity<>(riskModelScore, HttpStatus.OK);
    }

    private ResponseEntity<RiskModelScore> getResponseEntityByRetrofitResponse(Response<RiskModelScore> response) throws RiskEngineException {

        if (response.code() != HttpStatus.OK.value()) {
            try {
                String errorMessage = response.errorBody().string();
                log.error("Risk Engine  Error Message: {}", errorMessage);
                throw new RiskEngineException(response.code(), CreditsErrorMessagesEnum.DEFAULT_ERROR.getMessage());
            } catch (IOException e) {
                throw new RiskEngineException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            }
        }

        if (RiskEngineStatus.RUNNING.name().equalsIgnoreCase(response.body().getStatus())) {
            log.info(" .. Risk Engine running now  ..");
            throw new RiskEngineException(HttpStatus.INTERNAL_SERVER_ERROR.value(), RiskEngineStatus.RUNNING.name());
        }

        RiskModelScore riskModelScore = response.body();

        riskModelScore.getRiskEngineOffer().forEach(r -> r.setAmount(Double.valueOf(String.format("%.0f",
                r.getAmount()))));

        return new ResponseEntity<>(response.body(), HttpStatus.valueOf(response.code()));
    }

    public interface RiskModelApi {

        @GET("score/{id}")
        Call<RiskModelScore> getRiskModelByClientId(@Path("id") String id);

    }
}

