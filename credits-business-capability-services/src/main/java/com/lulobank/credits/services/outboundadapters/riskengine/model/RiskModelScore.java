package com.lulobank.credits.services.outboundadapters.riskengine.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class RiskModelScore {
    @SerializedName("results")
    List<RiskEngineOffer> riskEngineOffer;
    private String status;

    public RiskModelScore() {
        this.riskEngineOffer = new ArrayList<>();
    }
}
