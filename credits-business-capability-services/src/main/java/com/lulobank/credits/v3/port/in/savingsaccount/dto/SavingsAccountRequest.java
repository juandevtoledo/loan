package com.lulobank.credits.v3.port.in.savingsaccount.dto;

import com.lulobank.credits.v3.dto.ClientInformationV3;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingsAccountRequest {

    private String idClient;
    private ClientInformationV3 clientInformation;

}
