package com.lulobank.credits.services.inboundadapters.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientProductOfferRequest implements Command {

    private String idClient;

    public ClientProductOfferRequest(String idClient){
        this.idClient = idClient;
    }
}
