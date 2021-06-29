package com.lulobank.credits.v3.usecase;

import com.lulobank.credits.v3.events.CreateStatementMessage;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanStatement;
import com.lulobank.credits.v3.port.out.queue.NotificationLoanStatement;
import com.lulobank.credits.v3.util.EntitiesFactory;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IntentionLoanStatementUseCaseTest {

    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private NotificationLoanStatement notificationService;
    @Mock
    private CoreBankingService coreBankingService;

    @Captor
    protected ArgumentCaptor<CreateStatementMessage> loanStatementCreatedCaptor;

    @Captor
    protected ArgumentCaptor<String> statementDateCaptor;

    private IntentionLoanStatementUseCase intentionLoanStatementUseCaseTest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        intentionLoanStatementUseCaseTest = new IntentionLoanStatementUseCase(creditsV3Repository, notificationService, coreBankingService);
    }

    @Test
    public void setEventsOk_StatementDateCurrentPeriod() {
        LocalDateTime installmentDate = LocalDateTime.now().plusDays(3);
        setEventsOk(installmentDate, installmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }

    @Test
    public void setEventsOk_StatementDateNextPeriod() {
        LocalDateTime installmentDate = LocalDateTime.now().minusDays(3);
        setEventsOk(installmentDate, installmentDate.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }

    public void setEventsOk(LocalDateTime installmentDate, String expectedStatementDate) {
        CreditsV3Entity entity = EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer();
        List<CreditsV3Entity> creditsV3Entities = List.of(entity);
        when(creditsV3Repository.findByStatementsIndex(anyString())).thenReturn(creditsV3Entities);
        when(notificationService.requestLoanStatement(any())).thenReturn(Try.run(String::new));
        when(coreBankingService.getLoanStatement(any(), any(), statementDateCaptor.capture()))
                .thenReturn(Either.right(LoanStatement.builder()
                        .interestRate("16.50")
                        .build()));
        when(coreBankingService.getLoanInformation(any(), any())).thenReturn(Either.right(LoanInformation.builder()
                .installmentDate(installmentDate).build()));
        intentionLoanStatementUseCaseTest.execute("2020-01-01");
        verify(notificationService, times(1)).requestLoanStatement(loanStatementCreatedCaptor.capture());
        assertThat(loanStatementCreatedCaptor.getValue().getIdClient(), is(entity.getIdClient()));
        assertThat(loanStatementCreatedCaptor.getValue().getProductType(), is("LOAN_ACCOUNT"));
        assertThat(loanStatementCreatedCaptor.getValue().getReportType(), is("LOANSTATEMENTS"));
        assertThat(loanStatementCreatedCaptor.getValue().getData().get("automaticDebit"), is(entity.getAutomaticDebit()));
        assertThat(loanStatementCreatedCaptor.getValue().getData().get("idCreditCBS"), is(entity.getIdLoanAccountMambu()));
        assertThat(loanStatementCreatedCaptor.getValue().getData().get("idClientCBS"), is(entity.getIdClientMambu()));
        assertThat(loanStatementCreatedCaptor.getValue().getData().get("name"), is(entity.getClientInformation().getName()));
        assertThat(loanStatementCreatedCaptor.getValue().getData().get("interestRate"), is(entity.getAcceptOffer().getInterestRate().toString()));
        assertThat(statementDateCaptor.getValue(), is(expectedStatementDate));

    }

    @Test
    public void notSendEvent() {
        List<CreditsV3Entity> creditsV3Entities = List.empty();
        when(creditsV3Repository.findByStatementsIndex(anyString())).thenReturn(creditsV3Entities);
        intentionLoanStatementUseCaseTest.execute("2020-01-01");
        verify(notificationService, never()).requestLoanStatement(any());
    }

} 