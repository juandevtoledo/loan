package com.lulobank.credits.starter.v3.adapters.out.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.LoanStatus;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.tracing.DatabaseBrave;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_CREDIT;
import static com.lulobank.credits.starter.utils.Constants.ID_LOAN_ACCOUNT_MAMBU;
import static com.lulobank.credits.starter.utils.Constants.ID_OFFER;
import static com.lulobank.credits.starter.utils.Constants.ID_PRODUCT_OFFER;
import static com.lulobank.credits.starter.utils.Samples.creditsEntityBuilder;
import static com.lulobank.credits.v3.service.OffersTypeV3.COMFORTABLE_LOAN;
import static java.util.UUID.fromString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreditsAdapterV3RepositoryTest {
    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private DatabaseBrave databaseBrave;
    @Mock
    private PaginatedQueryList<CreditsEntity> queryList;
    @Mock
    private PaginatedScanList<CreditsEntity> scanList;
    private CreditsAdapterV3Repository creditsAdapterRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        creditsAdapterRepository = new CreditsAdapterV3Repository(dynamoDBMapper, databaseBrave);

        when(dynamoDBMapper.query(eq(CreditsEntity.class), any())).thenReturn(queryList);
        when(dynamoDBMapper.scan(eq(CreditsEntity.class), any())).thenReturn(scanList);
    }

    @Test
    public void findClientByOffer() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.queryOptional(Mockito.any())).thenReturn(Optional.of(creditsEntity));

        Option<CreditsV3Entity> entity = creditsAdapterRepository.findClientByOffer(fromString(ID_CREDIT), ID_CLIENT);

        assertThat("IdClient is right", entity.get().getIdClient(), is(ID_CLIENT));
        assertThat("IdClient is righT", entity.get().getIdCredit(), is(fromString(ID_CREDIT)));
    }

    @Test
    public void findByIdClient() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.query(Mockito.any())).thenReturn(Stream.of(creditsEntity));

        List<CreditsV3Entity> entity = creditsAdapterRepository.findByIdClient(ID_CLIENT);
        assertThat("IdClient is right", entity.get().getIdClient(), is(ID_CLIENT));
        assertThat("IdClient is right", entity.get().getIdCredit(), is(fromString(ID_CREDIT)));
    }

    @Test
    public void findOfferEntityV3ByIdClient() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.queryOptional(Mockito.any())).thenReturn(Optional.of(creditsEntity));

        Option<OfferEntityV3> offerEntityV3s = creditsAdapterRepository.findOfferEntityV3ByIdClient(ID_CLIENT, UUID.fromString(ID_CREDIT), ID_OFFER);
        assertThat("Offer Type is Correct", offerEntityV3s.get().getType(), is(COMFORTABLE_LOAN.name()));
        assertThat("Id Offer is Correct", offerEntityV3s.get().getIdOffer(), is(ID_OFFER));
    }

    @Test
    public void findByIdCreditAndIdLoanAccountMambu() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.queryOptional(Mockito.any())).thenReturn(Optional.of(creditsEntity));

        Option<CreditsV3Entity> entityOption = creditsAdapterRepository.findByIdCreditAndIdLoanAccountMambu(ID_CREDIT, ID_LOAN_ACCOUNT_MAMBU);

        assertThat(entityOption.isDefined(), is(true));
        assertThat(entityOption.get().getIdCredit(), is(UUID.fromString(ID_CREDIT)));
        assertThat(entityOption.get().getIdLoanAccountMambu(), is(ID_LOAN_ACCOUNT_MAMBU));
    }

    @Test
    public void findByIdProductOffer() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(scanList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.queryOptional(Mockito.any())).thenReturn(Optional.of(creditsEntity));

        Option<CreditsV3Entity> entityOption = creditsAdapterRepository.findByIdProductOffer(ID_PRODUCT_OFFER);

        assertThat(entityOption.isDefined(), is(true));
        assertThat(entityOption.get().getIdCredit(), is(UUID.fromString(ID_CREDIT)));
        assertThat(entityOption.get().getIdLoanAccountMambu(), is(ID_LOAN_ACCOUNT_MAMBU));
    }

    @Test
    public void findByIdProductOfferError() {
        when(scanList.stream()).thenReturn(Stream.empty());
        when(databaseBrave.queryOptional(Mockito.any())).thenReturn(Optional.empty());

        Option<CreditsV3Entity> entityOption = creditsAdapterRepository.findByIdProductOffer(ID_PRODUCT_OFFER);

        assertThat(entityOption.isDefined(), is(false));
        assertThat(entityOption.isEmpty(), is(true));
    }

    @Test
    public void saveWhenValidPayload() {
        when(databaseBrave.save(Mockito.any())).thenReturn(dynamoDBMapper::save);
        creditsAdapterRepository.save(new CreditsV3Entity());
        verify(databaseBrave, times(1)).save(Mockito.any());
        verify(dynamoDBMapper).save(Mockito.any());
    }

    @Test
    public void findByidClientAndIdLoanAccountMambuNotNull() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.query(Mockito.any())).thenReturn(Stream.of(creditsEntity));

        List<CreditsV3Entity> entity = creditsAdapterRepository.findByidClientAndIdLoanAccountMambuNotNull(ID_CLIENT);

        assertThat(entity.isEmpty(), is(false));
        assertThat("IdClient is right", entity.get().getIdClient(), is(ID_CLIENT));
        assertThat(entity.get().getIdLoanAccountMambu(), is(ID_LOAN_ACCOUNT_MAMBU));
        assertThat("IdClient is right", entity.get().getIdCredit(), is(fromString(ID_CREDIT)));

    }

    @Test
    public void findById_WhenCreditIsFound() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(dynamoDBMapper.load(any(CreditsEntity.class), any())).thenReturn(creditsEntity);
        when(databaseBrave.queryOptional(Mockito.any())).thenReturn(Optional.of(creditsEntity));

        Option<CreditsV3Entity> credit = creditsAdapterRepository.findById(ID_CREDIT);

        assertTrue(credit.isDefined());
        assertThat(credit.get().getIdCredit(), is(UUID.fromString(ID_CREDIT)));
    }

    @Test
    public void findById_WhenCreditNotFound() {
        when(dynamoDBMapper.load(any(CreditsEntity.class), any())).thenReturn(null);
        when(databaseBrave.queryOptional(Mockito.any())).thenReturn(Optional.empty());

        Option<CreditsV3Entity> credit = creditsAdapterRepository.findByIdLoanAccountMambu(ID_CREDIT);

        assertFalse(credit.isDefined());
        assertTrue(credit.isEmpty());
    }

    @Test
    public void findByStatementsIndex_WhenCreditIsFound() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.query(Mockito.any())).thenReturn(Stream.of(creditsEntity));

        List<CreditsV3Entity> credits = creditsAdapterRepository.findByStatementsIndex(ID_CREDIT);

        assertFalse(credits.isEmpty());
    }

    @Test
    public void findByStatementsIndex_WhenCreditNotFound() {
        when(queryList.stream()).thenReturn(Stream.empty());
        when(databaseBrave.query(Mockito.any())).thenReturn(Stream.empty());

        List<CreditsV3Entity> credits = creditsAdapterRepository.findByStatementsIndex(ID_CREDIT);

        assertTrue(credits.isEmpty());
    }

    @Test
    public void findByIdLoanAccountMambu_WhenCreditIsFound() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(scanList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.queryOptional(Mockito.any())).thenReturn(Optional.of(creditsEntity));

        Option<CreditsV3Entity> credit = creditsAdapterRepository.findByIdLoanAccountMambu(ID_LOAN_ACCOUNT_MAMBU);

        assertTrue(credit.isDefined());
        assertThat(credit.get().getIdLoanAccountMambu(), is(ID_LOAN_ACCOUNT_MAMBU));
    }

    @Test
    public void findByIdLoanAccountMambu_WhenCreditNotFound() {
        when(scanList.stream()).thenReturn(Stream.empty());
        when(databaseBrave.queryOptional(Mockito.any())).thenReturn(Optional.empty());

        Option<CreditsV3Entity> credit = creditsAdapterRepository.findByIdLoanAccountMambu(ID_LOAN_ACCOUNT_MAMBU);

        assertFalse(credit.isDefined());
        assertTrue(credit.isEmpty());
    }

    @Test
    public void findLoanActiveByIdClient_WhenCreditFound() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.query(Mockito.any())).thenReturn(Stream.of(creditsEntity));

        Option<CreditsV3Entity> entityOption = creditsAdapterRepository.findLoanActiveByIdClient(ID_CREDIT);

        assertThat(entityOption.isDefined(), is(true));
        assertThat(entityOption.get().getIdCredit(), is(UUID.fromString(ID_CREDIT)));
        assertThat(entityOption.get().getIdLoanAccountMambu(), is(ID_LOAN_ACCOUNT_MAMBU));
    }

    @Test
    public void findLoanActiveByIdClient_WhenCreditPending() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        creditsEntity.setLoanStatus(getLoanStatusByStatus("PENDING"));
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.query(Mockito.any())).thenReturn(Stream.of(creditsEntity));

        Option<CreditsV3Entity> entityOption = creditsAdapterRepository.findLoanActiveByIdClient(ID_CREDIT);

        assertThat(entityOption.isDefined(), is(true));
        assertThat(entityOption.get().getIdCredit(), is(UUID.fromString(ID_CREDIT)));
        assertThat(entityOption.get().getIdLoanAccountMambu(), is(ID_LOAN_ACCOUNT_MAMBU));
    }

    @Test
    public void findLoanActiveByIdClient_WhenCreditClosed() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        creditsEntity.setLoanStatus(getLoanStatusByStatus("CLOSED"));
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.query(Mockito.any())).thenReturn(Stream.of(creditsEntity));

        Option<CreditsV3Entity> entityOption = creditsAdapterRepository.findLoanActiveByIdClient(ID_CREDIT);

        assertThat(entityOption.isDefined(), is(false));
    }


    @Test
    public void findLoanActiveByIdClient_WhenLoanStatusNull() {
        CreditsEntity creditsEntity = creditsEntityBuilder();
        creditsEntity.setLoanStatus(null);
        when(queryList.stream()).thenReturn(Stream.of(creditsEntity));
        when(databaseBrave.query(Mockito.any())).thenReturn(Stream.of(creditsEntity));
        Option<CreditsV3Entity> entityOption = creditsAdapterRepository.findLoanActiveByIdClient(ID_CREDIT);
        assertThat(entityOption.isDefined(), is(false));
    }

    private LoanStatus getLoanStatusByStatus(String state) {
        LoanStatus loanStatus = new LoanStatus();
        loanStatus.setStatus(state);
        return loanStatus;
    }

}
