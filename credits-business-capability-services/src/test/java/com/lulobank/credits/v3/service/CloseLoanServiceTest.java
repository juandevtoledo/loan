package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.events.GoodStandingCertificateEvent;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.ReportingQueueService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

public class CloseLoanServiceTest {

    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private ReportingQueueService reportingQueueService;
    @Mock
    private SchedulerTransactionAsyncService schedulerAsyncService;
    @Captor
    private ArgumentCaptor<CreditsV3Entity> creditsV3EntityPersistCaptor;
    @Captor
    private ArgumentCaptor<GoodStandingCertificateEvent> goodStandingCertificateEventCaptor;
    @Captor
    private ArgumentCaptor<TransactionRequest> transactionRequestCaptor;
    private CloseLoanService closeLoanService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        closeLoanService = new CloseLoanService(creditsV3Repository, reportingQueueService, schedulerAsyncService);
    }

    @Test
    public void closed_WhenCreditFound() {
        CreditsV3Entity creditsV3Entity = creditsEntityWithAcceptOffer();
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(anyString(), anyString())).thenReturn(Option.of(creditsV3Entity));
        when(creditsV3Repository.save(creditsV3EntityPersistCaptor.capture())).thenReturn(Try.of(() -> creditsV3Entity));
        when(reportingQueueService.sendGoodStanding(goodStandingCertificateEventCaptor.capture())).thenReturn(Try.run(System.out::println));
        Mockito.doNothing().when(schedulerAsyncService).deleteTransaction(transactionRequestCaptor.capture());
        Either<UseCaseResponseError, CreditsV3Entity> response = closeLoanService.close(UUID.randomUUID().toString(), "ID_LOAN");
        assertThat(response.isRight(), is(true));
        assertEntitySaveCaptor(creditsV3EntityPersistCaptor.getValue());
        assertGoodStandingEvent(goodStandingCertificateEventCaptor.getValue());
        assertTransactionRequest(transactionRequestCaptor.getValue());
    }

    @Test
    public void closed_WhenCreditNotFound() {
        when(creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(anyString(), anyString())).thenReturn(Option.none());
        Either<UseCaseResponseError, CreditsV3Entity> response = closeLoanService.close(UUID.randomUUID().toString(), "ID_LOAN");
        assertThat(response.isLeft(), is(true));
        Mockito.verify(creditsV3Repository,never()).save(any());
        Mockito.verify(reportingQueueService,never()).sendGoodStanding(any());
        Mockito.verify(schedulerAsyncService,never()).deleteTransaction(any());
    }

    private void assertTransactionRequest(TransactionRequest transactionRequest) {
        assertThat(transactionRequest.getDayOfPay(), is(15));
        assertThat(transactionRequest.getIdClient(), is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
        assertThat(transactionRequest.getMetadata(), is("15#credits#SUBSCRIPTION"));
    }

    private void assertGoodStandingEvent(GoodStandingCertificateEvent goodStandingCertificateEvent) {
        assertThat(goodStandingCertificateEvent.getAmount(), is(BigDecimal.valueOf(1000000.0)));
        assertThat(goodStandingCertificateEvent.getIdClient(),is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
        assertThat(goodStandingCertificateEvent.getTypeReport(),is("GOODSTANDINGCERTIFICATE"));
        assertThat(goodStandingCertificateEvent.getIdLoanAccountMambu(),is("YAMW127"));
        assertThat(goodStandingCertificateEvent.getClientInformationByIdClient().getContent().getIdCard(),is("1999368732"));
        assertThat(goodStandingCertificateEvent.getClientInformationByIdClient().getContent().getName(),is("TAE NOM JBJQJ m"));
        assertThat(goodStandingCertificateEvent.getClientInformationByIdClient().getContent().getLastName(),is("TAE APE JBJQJ s"));
        assertThat(goodStandingCertificateEvent.getClientInformationByIdClient().getContent().getEmailAddress(),is("tae_vbgbp@mailinator.com"));
    }

    private void assertEntitySaveCaptor(CreditsV3Entity creditsV3EntityPersist) {
        assertThat(creditsV3EntityPersist.getStatementsIndex(), is(""));
        assertThat(creditsV3EntityPersist.getClosedDate().toLocalDate(), is(LocalDate.now()));
        assertThat(creditsV3EntityPersist.getLoanStatus().getStatus(), is("CLOSED"));
        assertThat(creditsV3EntityPersist.getLoanStatus().getCertificationSent(), is(true));
    }
}
