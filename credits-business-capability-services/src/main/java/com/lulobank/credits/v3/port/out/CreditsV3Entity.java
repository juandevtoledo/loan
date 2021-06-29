package com.lulobank.credits.v3.port.out;

import com.lulobank.credits.v3.dto.ClientInformationV3;
import com.lulobank.credits.v3.dto.CreditType;
import com.lulobank.credits.v3.dto.DecevalInformationV3;
import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.dto.LoanConditionsEntityV3;
import com.lulobank.credits.v3.dto.LoanRequestedV3;
import com.lulobank.credits.v3.dto.LoanStatusV3;
import com.lulobank.credits.v3.dto.ModifiedLoan;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreditsV3Entity {
    private UUID idCredit;
    private String idClient;
    private String idClientMambu;
    private String encodedKeyClientMambu;
    private String idLoanAccountMambu;
    private String encodedKeyLoanAccountMambu;
    private List<LoanConditionsEntityV3> loanConditionsList;
    private InitialOfferV3 initialOffer;
    private String purpose;
    private OfferEntityV3 acceptOffer;
    private List<FlexibleLoanV3> flexibleLoans;
    private ClientInformationV3 clientInformation;
    private LoanRequestedV3 loanRequested;
    private String idSavingAccount;
    private Boolean automaticDebit;
    private LoanStatusV3 loanStatus;
    private LocalDateTime closedDate;
    private DecevalInformationV3 decevalInformation;
    private LocalDateTime acceptDate;
    private Integer dayOfPay;
    private String statementsIndex;
    private String idProductOffer;
    private CreditType creditType;
    private String status;
    private List<ModifiedLoan> modifiedHistory;
    private String riskEngineDescription;
    private String riskEngineDetail;

    public List<ModifiedLoan> getModifiedHistory() {
        return Option.of(modifiedHistory).getOrElse(() -> {
          modifiedHistory = new ArrayList<>();
          return modifiedHistory;
        });
    }
}
