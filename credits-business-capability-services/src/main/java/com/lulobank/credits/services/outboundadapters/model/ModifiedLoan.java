package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.lulobank.credits.services.outboundadapters.dynamoconverter.LocalDateTimeConverter;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@DynamoDBDocument
public class ModifiedLoan {
    private String idLoanAccountMambu;
    private BigDecimal amount;
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime acceptDate;
    private BigDecimal interestRate;
    private BigDecimal monthlyNominalRate;
    private BigDecimal annualNominalRate;
    private String modificationType;
    private Integer installments;
}
