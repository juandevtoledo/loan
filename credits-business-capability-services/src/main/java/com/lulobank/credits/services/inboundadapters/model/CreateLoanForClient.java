package com.lulobank.credits.services.inboundadapters.model;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
//TODO cambiar el nombre de la clase
public class CreateLoanForClient implements Command {
    private String idClient;
    private String firstName;
    private String lastName;
    private String middleName;
    private String emailAddress;
    private String mobilePhone;
    private String gender;
    private String idCredit;
    private List<LoanConditions> loanConditionsList;
}