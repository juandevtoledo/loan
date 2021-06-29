package com.lulobank.credits.starter.v3.adapters.out.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.InitialOffer;
import com.lulobank.credits.starter.v3.adapters.out.dynamo.dto.CreditsDto;
import com.lulobank.credits.starter.v3.mappers.CreditsEntityMapper;
import com.lulobank.credits.starter.v3.mappers.OffersEntityMapper;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.tracing.DatabaseBrave;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Boolean.FALSE;

@CustomLog
public class CreditsAdapterV3Repository implements CreditsV3Repository {

    private static final String ID_CREDIT_KEY = ":idCredit";
    private static final String ID_CLIENT_KEY = ":idClient";
    private static final String ID_LOAN_ACCOUNT_MAMBU_KEY = ":idLoanAccountMambu";
    private static final String ID_LOAN_ACCOUNT_MAMBU_INDEX = "idLoanAccountMambu-index";
    private static final String ID_CLIENT_INDEX = "idClient-index";
    public static final String FILTER_EXPRESSION_ID_CLIENT = "idClient = " + ID_CLIENT_KEY;
    private static final String FILTER_EXPRESSION_ID_LOAN_ACCOUNT_MAMBU = "idLoanAccountMambu = " + ID_LOAN_ACCOUNT_MAMBU_KEY;


    private final DynamoDBMapper dynamoDBMapper;
    private final DatabaseBrave databaseBrave;

    public CreditsAdapterV3Repository(DynamoDBMapper dynamoDBMapper, DatabaseBrave databaseBrave) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.databaseBrave = databaseBrave;
    }

    @Override
    public Option<CreditsV3Entity> findById(String idCredit) {
        Optional<CreditsEntity> entity = Optional.ofNullable(dynamoDBMapper.load(CreditsEntity.class, UUID.fromString(idCredit)));
        return Option.ofOptional(databaseBrave.queryOptional(() -> entity))
                .map(CreditsEntityMapper.INSTANCE::toCreditsEntity);
    }

    @Override
    public Option<CreditsV3Entity> findClientByOffer(UUID idCredit, String idClient) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(ID_CREDIT_KEY, new AttributeValue().withS(idCredit.toString()));
        attributeValues.put(ID_CLIENT_KEY, new AttributeValue().withS(idClient));

        DynamoDBQueryExpression<CreditsEntity> queryExpression = new DynamoDBQueryExpression<CreditsEntity>()
                .withKeyConditionExpression("idCredit = " + ID_CREDIT_KEY)
                .withFilterExpression("idClient = " + ID_CLIENT_KEY + " and attribute_not_exists(idLoanAccountMambu)")
                .withExpressionAttributeValues(attributeValues);

        Optional<CreditsEntity> offer = dynamoDBMapper.query(CreditsEntity.class, queryExpression).stream().findFirst();
        return Option.ofOptional(databaseBrave.queryOptional(() -> offer))
                .map(CreditsEntityMapper.INSTANCE::toCreditsEntity);
    }

    @Override
    public List<CreditsV3Entity> findByIdClient(String idClient) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(ID_CLIENT_KEY, new AttributeValue().withS(idClient));

        DynamoDBQueryExpression<CreditsEntity> queryExpression = new DynamoDBQueryExpression<CreditsEntity>()
                .withIndexName(ID_CLIENT_INDEX)
                .withKeyConditionExpression(FILTER_EXPRESSION_ID_CLIENT)
                .withConsistentRead(false)
                .withExpressionAttributeValues(attributeValues);

        return Stream.ofAll(databaseBrave.query(() -> dynamoDBMapper.query(CreditsEntity.class, queryExpression).stream()))
                .map(CreditsEntityMapper.INSTANCE::toCredit)
                .toList();
    }

    @Override
    public Option<OfferEntityV3> findOfferEntityV3ByIdClient(String idClient, UUID idCredit, String idOffer) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(ID_CREDIT_KEY, new AttributeValue().withS(idCredit.toString()));
        attributeValues.put(ID_CLIENT_KEY, new AttributeValue().withS(idClient));

        DynamoDBQueryExpression<CreditsEntity> queryExpression = new DynamoDBQueryExpression<CreditsEntity>()
                .withKeyConditionExpression("idCredit = " + ID_CREDIT_KEY)
                .withFilterExpression("idClient = " + ID_CLIENT_KEY + " and attribute_not_exists(idLoanAccountMambu)")
                .withExpressionAttributeValues(attributeValues);

        Optional<CreditsEntity> offer = dynamoDBMapper.query(CreditsEntity.class, queryExpression).stream().findFirst();

        return Option.ofOptional(databaseBrave.queryOptional(() -> offer)
                .map(CreditsEntity::getInitialOffer)
                .map(InitialOffer::getOfferEntities)
                .flatMap(offerEntities ->
                        offerEntities.stream()
                                .filter(offerEntity -> idOffer.equals(offerEntity.getIdOffer()))
                                .findFirst())
                .map(OffersEntityMapper.INSTANCE::offerEntityV3From));
    }

    @Override
    public Try<CreditsV3Entity> save(CreditsV3Entity creditsV3Entity) {
        return Try.of(() -> CreditsEntityMapper.INSTANCE.toCreditsDto(creditsV3Entity))
                .flatMap(creditsDto -> Try.run(() -> databaseBrave.save(credits()).accept(creditsDto)))
                .map(success -> creditsV3Entity)
                .onFailure(error -> log.error("Error trying to persist in dynamo , msg : {} ", error.getMessage(), error));
    }

    @Override
    public List<CreditsV3Entity> findByStatementsIndex(String statementsIndex) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":statementsIndex", new AttributeValue().withS(statementsIndex));

        DynamoDBQueryExpression<CreditsEntity> queryExpression = new DynamoDBQueryExpression<CreditsEntity>()
                .withIndexName("statementsIndex-index")
                .withKeyConditionExpression("statementsIndex = :statementsIndex")
                .withConsistentRead(false)
                .withExpressionAttributeValues(attributeValues);

        return Stream.ofAll(databaseBrave.query(() -> dynamoDBMapper.query(CreditsEntity.class, queryExpression).stream()))
                .map(CreditsEntityMapper.INSTANCE::toCredit)
                .toList();
    }

    @Override
    public Option<CreditsV3Entity> findByIdCreditAndIdLoanAccountMambu(String idCredit, String idCreditCBS) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(ID_CREDIT_KEY, new AttributeValue().withS(idCredit));
        attributeValues.put(":idCreditCBS", new AttributeValue().withS(idCreditCBS));

        DynamoDBQueryExpression<CreditsEntity> queryExpression = new DynamoDBQueryExpression<CreditsEntity>()
                .withKeyConditionExpression("idCredit = :idCredit")
                .withFilterExpression("idLoanAccountMambu = :idCreditCBS")
                .withExpressionAttributeValues(attributeValues);

        Optional<CreditsEntity> optionalEntity = dynamoDBMapper.query(CreditsEntity.class, queryExpression).stream().findFirst();

        return Option.ofOptional(databaseBrave.queryOptional(() -> optionalEntity))
                .map(CreditsEntityMapper.INSTANCE::toCredit)
                .toOption();
    }

    @Override
    public Option<CreditsV3Entity> findByIdProductOffer(String idProductOffer) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":idProductOffer", new AttributeValue().withS(idProductOffer));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("idProductOffer = :idProductOffer")
                .withExpressionAttributeValues(attributeValues);

        Optional<CreditsEntity> optionalEntity = dynamoDBMapper.scan(CreditsEntity.class, scanExpression).stream().findFirst();

        return Option.ofOptional(databaseBrave.queryOptional(() -> optionalEntity))
                .map(CreditsEntityMapper.INSTANCE::toCredit)
                .toOption();
    }

    @Override
    public List<CreditsV3Entity> findByidClientAndIdLoanAccountMambuNotNull(String idClient) {

        return Stream.ofAll(databaseBrave.query(() -> dynamoDBMapper.query(CreditsEntity.class, getDBQueryWithClientIndex(idClient)).stream()))
                .map(CreditsEntityMapper.INSTANCE::toCredit)
                .toList();
    }

    @Override
    public Option<CreditsV3Entity> findByIdLoanAccountMambu(String idLoanAccountMambu) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(ID_LOAN_ACCOUNT_MAMBU_KEY, new AttributeValue().withS(idLoanAccountMambu));

        DynamoDBQueryExpression<CreditsEntity> queryExpression = new DynamoDBQueryExpression<CreditsEntity>()
                .withIndexName(ID_LOAN_ACCOUNT_MAMBU_INDEX)
                .withKeyConditionExpression(FILTER_EXPRESSION_ID_LOAN_ACCOUNT_MAMBU)
                .withConsistentRead(false)
                .withExpressionAttributeValues(attributeValues);

        Optional<CreditsEntity> optionalEntity = dynamoDBMapper.query(CreditsEntity.class, queryExpression).stream().findFirst();

        return Option.ofOptional(databaseBrave.queryOptional(() -> optionalEntity))
                .map(CreditsEntityMapper.INSTANCE::toCredit)
                .toOption();
    }

    @Override
    public Option<CreditsV3Entity> findLoanActiveByIdClient(String idClient) {

        return Option.ofOptional(databaseBrave.query(() -> dynamoDBMapper.query(CreditsEntity.class, getDBQueryWithClientIndex(idClient)).stream())
                .map(CreditsEntityMapper.INSTANCE::toCredit)
                .filter(this::isActiveLoan)
                .findFirst());
    }

    private DynamoDBQueryExpression<CreditsEntity> getDBQueryWithClientIndex(String idClient) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(ID_CLIENT_KEY, new AttributeValue().withS(idClient));


        return new DynamoDBQueryExpression<CreditsEntity>()
                .withIndexName("idClient-index")
                .withKeyConditionExpression("idClient = :idClient")
                .withFilterExpression("attribute_exists(idLoanAccountMambu)")
                .withConsistentRead(false)
                .withExpressionAttributeValues(attributeValues);
    }

    private boolean isActiveLoan(CreditsV3Entity creditsV3Entity) {
        return Option.of(creditsV3Entity.getLoanStatus())
                .filter(loanStatus -> Objects.nonNull(loanStatus.getStatus()))
                .map(loanStatus -> Pattern.compile("ACTIVE|PARTIAL_APPLICATION|PENDING|PENDING_APPROVAL|ACTIVE_IN_ARREARS|APPROVED").matcher(loanStatus.getStatus()))
                .map(Matcher::matches)
                .getOrElse(FALSE);
    }

    @NotNull
    private Consumer<CreditsDto> credits() {
        return dynamoDBMapper::save;
    }
}
