package com.lulobank.credits.services.inboundadapters.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//TODO cambiar el nombre de la clase
public class CreateLoanClientResponse  {
    private String idClient;
    private String idClientHash;
    private String creditAccountNumber;
    private String creditAccountHash;
    private String savingAccountNumber;
    private String savingAccountHash;

}
