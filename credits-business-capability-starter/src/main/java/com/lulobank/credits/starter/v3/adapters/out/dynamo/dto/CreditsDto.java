package com.lulobank.credits.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.lulobank.credits.services.outboundadapters.dynamoconverter.LocalDateTimeConverter;
import com.lulobank.credits.services.outboundadapters.model.ClientInformation;
import com.lulobank.credits.services.outboundadapters.model.DecevalInformation;
import com.lulobank.credits.services.outboundadapters.model.FlexibleLoan;
import com.lulobank.credits.services.outboundadapters.model.InitialOffer;
import com.lulobank.credits.services.outboundadapters.model.LoanConditionsEntity;
import com.lulobank.credits.services.outboundadapters.model.LoanRequested;
import com.lulobank.credits.services.outboundadapters.model.LoanStatus;
import com.lulobank.credits.services.outboundadapters.model.ModifiedLoan;
import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import com.lulobank.credits.services.utils.DynamoDBProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@DynamoDBTable(tableName = DynamoDBProperties.TABLE_NAME)
public class CreditsDto {
    @DynamoDBHashKey
    private UUID idCredit;
    private String idClient;
    private String idClientMambu;
    private String encodedKeyClientMambu;
    private String idLoanAccountMambu;
    private String encodedKeyLoanAccountMambu;
    private List<LoanConditionsEntity> loanConditionsList;
    private InitialOffer initialOffer;
    private String purpose;
    private OfferEntity acceptOffer;
    private List<FlexibleLoan> flexibleLoans;
    private ClientInformation clientInformation;
    private LoanRequested loanRequested;
    private String idSavingAccount;
    private Boolean automaticDebit;
    private LoanStatus loanStatus;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime closedDate;
    private DecevalInformation decevalInformation;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime acceptDate;
    private Integer dayOfPay;
    private String statementsIndex;
    private String idProductOffer;
    private String creditType;
    private String status;
    private List<ModifiedLoan> modifiedHistory;
    private String riskEngineDescription;
    private String riskEngineDetail;
}