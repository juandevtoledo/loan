package com.lulobank.credits.services.features.initialoffer;

public enum RiskModelResponse {

    OK("Solicitud Aprobada"),
    CO("Contra Oferta"),
    KO("Solicitud Denegada");

    private String description;

    RiskModelResponse(String description) {
        this.description = description;
    }

}
