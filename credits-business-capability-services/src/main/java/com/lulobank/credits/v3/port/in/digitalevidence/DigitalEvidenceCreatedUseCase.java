package com.lulobank.credits.v3.port.in.digitalevidence;

import com.lulobank.credits.v3.dto.LoanStatusV3;
import com.lulobank.credits.v3.exception.AcceptLoanTransactionException;
import com.lulobank.credits.v3.mapper.DisbursementLoanMapper;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.credits.v3.port.out.ClientAlertsAsyncService;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.NotificationV3Service;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.util.UseCase;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;

import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper.SchedulerAutomaticDebitMapper.createTransactionRequest;

@CustomLog
public class DigitalEvidenceCreatedUseCase implements UseCase<DigitalEvidenceCreatedMessage,Try<Void>> {

	private static final String ERROR_CREATING_LOAN_IN_PROVIDER_MESSAGE = "Error creating loan in provider for client, idClient : %s";
	private static final String CREDIT_NOT_FOUND_MESSAGE = "Credit not found, idClient : %s ";

	private final CreditsV3Repository creditsV3Repository;
	private final LoanV3Service loanV3Service;
	private final NotificationV3Service notificationV3Service;
	private final ClientAlertsAsyncService clientAlertsAsyncService;
	private final SchedulerTransactionAsyncService schedulerAutomaticDebitAsyncService;

	public DigitalEvidenceCreatedUseCase(CreditsV3Repository creditsV3Repository,
										 LoanV3Service loanV3Service,
										 NotificationV3Service notificationV3Service, 
										 ClientAlertsAsyncService clientAlertsAsyncService,
										 SchedulerTransactionAsyncService schedulerAutomaticDebitAsyncService) {
		this.creditsV3Repository = creditsV3Repository;
		this.loanV3Service = loanV3Service;
		this.notificationV3Service = notificationV3Service;
		this.clientAlertsAsyncService = clientAlertsAsyncService;
		this.schedulerAutomaticDebitAsyncService = schedulerAutomaticDebitAsyncService;
	}

	@Override
	public Try<Void> execute(DigitalEvidenceCreatedMessage digitalEvidenceCreatedMessage) {

		return Try.run(() -> Try.of(() -> digitalEvidenceCreatedMessage)
                .filter(DigitalEvidenceCreatedMessage::isSuccess)
                .flatMap(event -> findCreditEntity(digitalEvidenceCreatedMessage))
                .map(this::createLoanTransaction)
                .map(transaction -> mapSavingsAccount(transaction, digitalEvidenceCreatedMessage))
                .flatMap(this::disbursementLoan)
                .peek(transaction -> creditsV3Repository.save(transaction.getEntity()))
                .peek(notificationV3Service::loanCreatedNotification)
                .peek(this::schedulerNotification)
				.peek(this::sendLoanFinishedNotification)
                .getOrElseThrow(exception -> exception)).onFailure(error -> log.error("Error creating the loan, idClient : {}, error : {} ",
				digitalEvidenceCreatedMessage.getIdClient(), error.getMessage(), error));
	}
	
	private void schedulerNotification(LoanTransaction transaction) {
		if (transaction.getEntity().getAutomaticDebit()) {
			schedulerAutomaticDebitAsyncService.createTransaction((createTransactionRequest(transaction.getEntity())));
		}
    }


	private LoanTransaction mapSavingsAccount(LoanTransaction transaction,
			DigitalEvidenceCreatedMessage digitalEvidenceCreatedMessage) {
		SavingsAccountResponse savingsAccountResponse = new SavingsAccountResponse();
		savingsAccountResponse.setAccountId(digitalEvidenceCreatedMessage.getAccountId());
		savingsAccountResponse.setIdCbs(digitalEvidenceCreatedMessage.getIdCbs());
		return transaction.setSavingsAccountResponse(savingsAccountResponse);
	}

	private LoanTransaction createLoanTransaction(CreditsV3Entity creditsV3Entity) {
		return new LoanTransaction().setCreditsV3Entity(creditsV3Entity);
	}

	private Try<LoanTransaction> disbursementLoan(LoanTransaction transaction) {
		
		return loanV3Service.disbursementLoan(
				DisbursementLoanMapper.INSTANCE.creditsV3EntityToDisbursementLoanRequest(transaction.getEntity()))
				.map(status -> transaction.setCreditsV3Entity(setAccountState(transaction.getEntity(), status)))
				.toTry(() -> new AcceptLoanTransactionException(
						String.format(ERROR_CREATING_LOAN_IN_PROVIDER_MESSAGE, transaction.getEntity().getIdClient())));
	}
	
	private CreditsV3Entity setAccountState(CreditsV3Entity creditsV3Entity, String status) {
		Option.of(creditsV3Entity.getLoanStatus())
				.onEmpty(LoanStatusV3::new)
				.peek(loanStatus -> loanStatus.setStatus(status))
				.peek(creditsV3Entity::setLoanStatus);
		return creditsV3Entity;
	}

	private Try<CreditsV3Entity> findCreditEntity(DigitalEvidenceCreatedMessage digitalEvidenceCreatedMessage) {
		return creditsV3Repository
				.findById(digitalEvidenceCreatedMessage.getIdCredit())
				.onEmpty(() -> log
						.info(String.format(CREDIT_NOT_FOUND_MESSAGE, digitalEvidenceCreatedMessage.getIdClient())))
				.toTry(() -> new AcceptLoanTransactionException(
						String.format(CREDIT_NOT_FOUND_MESSAGE, digitalEvidenceCreatedMessage.getIdClient())));
	}

	private void sendLoanFinishedNotification(LoanTransaction loanTransaction) {
		log.info("Message sent to notify loan creation finished.");
		clientAlertsAsyncService.sendCreditFinishedNotification(loanTransaction);
	}
}
