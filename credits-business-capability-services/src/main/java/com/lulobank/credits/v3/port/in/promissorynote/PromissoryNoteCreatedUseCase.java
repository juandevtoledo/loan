package com.lulobank.credits.v3.port.in.promissorynote;

import com.lulobank.credits.v3.exception.AcceptLoanTransactionException;
import com.lulobank.credits.v3.mapper.PromissoryNoteMapper;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.NotificationV3Service;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.util.UseCase;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;

@CustomLog
public class PromissoryNoteCreatedUseCase implements UseCase<CreatePromissoryNoteResponseMessage,Try<Void>> {

    private static final String CREDIT_NOT_FOUND_MESSAGE = "Credit not found, idClient : %s ";

    private final CreditsV3Repository creditsV3Repository;
    private final NotificationV3Service notificationV3Service;

    public PromissoryNoteCreatedUseCase(CreditsV3Repository creditsV3Repository,
                                        NotificationV3Service notificationV3Service) {
        this.creditsV3Repository = creditsV3Repository;
        this.notificationV3Service = notificationV3Service;
    }

    @Override
    public Try<Void> execute(CreatePromissoryNoteResponseMessage createPromissoryNoteResponseMessage) {
        return Try.run(() -> findCreditEntity(createPromissoryNoteResponseMessage)
                .map(creditsV3Entity -> addPromissoryNoteResponse(creditsV3Entity, createPromissoryNoteResponseMessage))
                .peek(creditsV3Repository::save)
                .map(this::creatLoanTransaction)
                .peek(loanTransaction -> requestDigitalEvidence(loanTransaction,
                        createPromissoryNoteResponseMessage))
                .getOrElseThrow(exception -> exception)).onFailure(error -> log.error(String.format("Error with Client: %s ,in PromissoryNoteCreatedEventHandler: %s",
                createPromissoryNoteResponseMessage.getIdClient(), error.getMessage())));
    }

    private CreditsV3Entity addPromissoryNoteResponse(CreditsV3Entity creditsV3Entity, CreatePromissoryNoteResponseMessage createPromissoryNoteResponseMessage) {
        creditsV3Entity.setDecevalInformation(PromissoryNoteMapper.INSTANCE
                .toDecevalInformationV3(createPromissoryNoteResponseMessage));
        return creditsV3Entity;
    }

    private void requestDigitalEvidence(LoanTransaction transaction, CreatePromissoryNoteResponseMessage createPromissoryNoteResponseMessage) {
        SavingsAccountResponse savingsAccountResponse =new SavingsAccountResponse();
        savingsAccountResponse.setAccountId(createPromissoryNoteResponseMessage.getAccountId());
        savingsAccountResponse.setIdCbs(createPromissoryNoteResponseMessage.getIdCbs());
        transaction.setSavingsAccountResponse(savingsAccountResponse);
        Option.of(transaction)
                .peek(loanTransaction -> notificationV3Service.requestDigitalEvidence(loanTransaction, createPromissoryNoteResponseMessage.getHeadersToSQS()))
                .peek(loanTransaction -> log.info("Request to digital evidence was sent"));
    }

    private LoanTransaction creatLoanTransaction(CreditsV3Entity creditsV3Entity) {
        return new LoanTransaction().setCreditsV3Entity(creditsV3Entity);
    }

    private Try<CreditsV3Entity> findCreditEntity(CreatePromissoryNoteResponseMessage createPromissoryNoteResponseMessage) {
        return creditsV3Repository
                .findById(createPromissoryNoteResponseMessage.getIdCredit())
                .onEmpty(() -> log.info(String.format(CREDIT_NOT_FOUND_MESSAGE, createPromissoryNoteResponseMessage.getIdClient())))
                .toTry(() -> new AcceptLoanTransactionException(String.format(CREDIT_NOT_FOUND_MESSAGE, createPromissoryNoteResponseMessage.getIdClient())));
    }


}
