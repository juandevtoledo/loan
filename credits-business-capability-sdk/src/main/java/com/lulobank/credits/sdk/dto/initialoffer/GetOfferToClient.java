package com.lulobank.credits.sdk.dto.initialoffer;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetOfferToClient implements Command {

    private String idClient;
    private Double amount;
    private String purpose;

    public GetOfferToClient(){
    }

    public GetOfferToClient(String idClient, Double amount, String purpose) {
        this.idClient = idClient;
        this.amount = amount;
        this.purpose = purpose;
    }
}
