package com.lulobank.credits.v3.usecase;

import com.lulobank.credits.v3.mapper.LoanStatementCreatedMapper;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanStatement;
import com.lulobank.credits.v3.port.out.queue.NotificationLoanStatement;
import com.lulobank.credits.v3.util.StatementsIndex;
import com.lulobank.credits.v3.util.UseCaseEvent;
import io.vavr.control.Try;
import lombok.CustomLog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@CustomLog
public class IntentionLoanStatementUseCase implements UseCaseEvent<String> {

    private final CreditsV3Repository creditsV3Repository;
    private final NotificationLoanStatement notificationService;
    private final CoreBankingService coreBankingService;

    public IntentionLoanStatementUseCase(CreditsV3Repository creditsV3Repository, NotificationLoanStatement notificationService, CoreBankingService coreBankingService) {
        this.creditsV3Repository = creditsV3Repository;
        this.notificationService = notificationService;
        this.coreBankingService = coreBankingService;
    }

    @Override
    public void execute(String command) {
        log.info("Init process to create loan statement by Clients");
        creditsV3Repository.findByStatementsIndex(StatementsIndex.getFilterExpression())
        		.forEach(creditsV3Entity -> coreBankingService.getLoanInformation(creditsV3Entity.getIdLoanAccountMambu(), creditsV3Entity.getIdClientMambu())
                        .peekLeft(loanStatementInformation -> log.info("Error getting loan information to create statement, idClient : {}  ", creditsV3Entity.getIdClient()))
                        .map(this::calculateStatementDate)
                        .flatMap(statementDate -> coreBankingService.getLoanStatement(creditsV3Entity.getIdLoanAccountMambu(), creditsV3Entity.getIdClientMambu(), statementDate))
                        .peek(loanStatementInformation -> log.info("Client Found to create statements, idClient : {}  ", creditsV3Entity.getIdClient()))
                        .map(loanStatementInformation -> sendMessageToReport(creditsV3Entity, loanStatementInformation)));
    }

    private Try<Void> sendMessageToReport(CreditsV3Entity creditsV3Entity, LoanStatement loanStatementInformation) {
        return Try.of(() -> LoanStatementCreatedMapper.loanStatementCreatedFrom(creditsV3Entity, loanStatementInformation))
                .flatMap(notificationService::requestLoanStatement)
                .onFailure(error -> log.error("Error sending request to create statement Loan by IdClient : {}  , msg : {} ", creditsV3Entity.getIdClient(), error.getMessage()));
    }
    
    private String calculateStatementDate(LoanInformation loanInformation) {
        return LocalDate.now().isAfter(loanInformation.getInstallmentDate().toLocalDate()) ?
                loanInformation.getInstallmentDate().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM")) :
                loanInformation.getInstallmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}
