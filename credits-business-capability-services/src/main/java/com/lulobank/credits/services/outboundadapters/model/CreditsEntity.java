package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.lulobank.credits.services.outboundadapters.dynamoconverter.LocalDateTimeConverter;
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
public class CreditsEntity {
    @DynamoDBHashKey
    private UUID idCredit;
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "idClient-index")
    private String idClient;
    private String idClientMambu;
    private String encodedKeyClientMambu;
    private String idLoanAccountMambu;
    private String encodedKeyLoanAccountMambu;
    private List<LoanConditionsEntity> loanConditionsList;
    private InitialOffer initialOffer;
    //TO DO: Eliminar este objeto cuando se elemine la versión V1, debido a que este Objeto se mueve al initialOffer
    private List<OfferEntity> offerEntities;
    private String purpose;
    private OfferEntity acceptOffer;
    private List<FlexibleLoan> flexibleLoans;
    private ClientInformation clientInformation;
    private LoanRequested loanRequested;
    private String idSavingAccount;
    private Boolean automaticDebit;
    private LoanStatus loanStatus;
    private LocalDateTime closedDate;
    private DecevalInformation decevalInformation;
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime acceptDate;

    public CreditsEntity(UUID idCredit) {
        this.idCredit = idCredit;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    public LocalDateTime getClosedDate() {
        return closedDate;
    }

    private Integer dayOfPay;
    private String statementsIndex;
    private String idProductOffer;
    private String creditType;
    private List<ModifiedLoan> modifiedHistory;
    private String status;
}