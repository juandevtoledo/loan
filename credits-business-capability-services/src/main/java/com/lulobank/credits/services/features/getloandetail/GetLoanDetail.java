package com.lulobank.credits.services.features.getloandetail;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetLoanDetail implements Command {
    private String idClient;

    public GetLoanDetail(String idClient) {
        this.idClient = idClient;
    }
}
