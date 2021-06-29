package com.lulobank.credits.starter.v3.adapters.out.sqs;

import static com.lulobank.credits.starter.utils.Constants.AMOUNT;
import static com.lulobank.credits.starter.utils.Constants.DECEVAL_CLIENT_ACCOUNT_ID;
import static com.lulobank.credits.starter.utils.Constants.DECEVAL_CONFIRMATION_LOAN_OTP;
import static com.lulobank.credits.starter.utils.Constants.DECEVAL_CORRELATION_ID;
import static com.lulobank.credits.starter.utils.Constants.DECEVAL_PROMISSORY_NOTE_ID;
import static com.lulobank.credits.starter.utils.Constants.ID_CARD;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.INSTALLMENT;
import static com.lulobank.credits.starter.utils.Constants.LAST_NAME;
import static com.lulobank.credits.starter.utils.Constants.NAME;
import static com.lulobank.credits.starter.utils.Samples.loanTransactionBuilder;
import static com.lulobank.credits.starter.utils.Samples.savingsAccountResponseBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;

import com.lulobank.credits.services.events.ApprovedLoanDocument;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.Event;

public class ApprovedLoanDocumentEventTest {

	private static final String DATE_FORMAT = "dd-MMM-yyyy HH:mm";

	private ApprovedLoanDocumentEvent subject;
	private String endpoint = "http://sqs.endpoint.com";

	@Before
	public void setup() {
		subject = new ApprovedLoanDocumentEvent(endpoint);
	}

	@Test
	public void creditGrantingEventTest() {

		LoanTransaction loanTransaction = loanTransactionBuilder(savingsAccountResponseBuilder());
		Event<ApprovedLoanDocument> event = subject.map(loanTransaction);
		assertThat("event name is right", event.getEventType(), is(ApprovedLoanDocument.class.getSimpleName()));
		assertThat("idCard is right", event.getPayload().getClientInfo().getIdCard(), is(ID_CARD));
		assertThat("idClient is right", event.getPayload().getClientInfo().getIdClient(), is(ID_CLIENT));
		assertThat("name is right", event.getPayload().getClientInfo().getName(), is(NAME));
		assertThat("lastName is right", event.getPayload().getClientInfo().getLastName(), is(LAST_NAME));

		assertThat("decevalClientAccountId is right", event.getPayload().getDecevalInformation().getClientAccountId(),
				is(String.valueOf(DECEVAL_CLIENT_ACCOUNT_ID)));
		assertThat("decevalConfirmationLoanOtp is right",
				event.getPayload().getDecevalInformation().getConfirmationLoanOTP(), is(DECEVAL_CONFIRMATION_LOAN_OTP));
		assertThat("decevalCorrelationId is right", event.getPayload().getDecevalInformation().getDecevalId(),
				is(DECEVAL_CORRELATION_ID));
		assertThat("decevalPromissoryNoteId is right", event.getPayload().getDecevalInformation().getPromissoryNoteId(),
				is(String.valueOf(DECEVAL_PROMISSORY_NOTE_ID)));

		assertThat("approvedAmount is right", event.getPayload().getApprovedAmount(), is(AMOUNT));
		assertThat("installment is right", event.getPayload().getInstallments(), is(INSTALLMENT));
		assertThat("acceptOfferDateTime is right", event.getPayload().getAcceptOfferDateTime(),
				is(loanTransaction.getEntity().getAcceptDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT))));
	}

}
