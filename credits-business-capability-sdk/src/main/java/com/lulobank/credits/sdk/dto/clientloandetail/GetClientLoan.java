package com.lulobank.credits.sdk.dto.clientloandetail;

import com.lulobank.core.Command;
import lombok.Getter;

@Getter
public class GetClientLoan implements Command {

    private final String idClient;

    public GetClientLoan(String idClient) {
        this.idClient = idClient;
    }
}
