package com.lulobank.credits.services.features.riskmodelscore.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientProductOffer implements Command {

    private String idClient;

    public ClientProductOffer(String idClient){
        this.idClient = idClient;
    }
}
