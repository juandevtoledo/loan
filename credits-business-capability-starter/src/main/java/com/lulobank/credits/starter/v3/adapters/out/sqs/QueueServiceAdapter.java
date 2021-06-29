package com.lulobank.credits.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.queue.NotificationV3Service;
import com.lulobank.credits.v3.service.LoanTransaction;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;

import java.util.Map;


@AllArgsConstructor
public class QueueServiceAdapter implements NotificationV3Service {

    private final ClientCBSCreatedEvent clientCBSCreatedEvent;
    private final PersistLoanDocumentEvent persistLoanDocumentEvent;
    private final SqsBraveTemplate sqsTemplate;
    private final ApprovedLoanDocumentEvent approvedLoanDocumentEvent;
    private final SendSignedDocumentGroupSqs sendSignedDocumentGroupSqs;
    private final RiskEngineResultEventV2Event riskEngineResultEventv2Event;

	@Override
	public void loanCreatedNotification(LoanTransaction event) {

		Option.of(event)
				.peek(e -> clientCBSCreatedEvent.send(e,
						r -> sqsTemplate.convertAndSend(r.getEndpoint(), r.getEvent())))
				.peek(e -> sendSignedDocumentGroupSqs.send(e,
						r -> sqsTemplate.convertAndSend(r.getEndpoint(), r.getEvent())));
	}

	@Override
	public void requestDigitalEvidence(LoanTransaction event, Map<String, Object> auth) {
		Option.of(event)
				.peek(e -> approvedLoanDocumentEvent.send(e,
						r -> sqsTemplate.convertAndSend(r.getEndpoint(), r.getEvent(), auth)))
				.peek(e -> persistLoanDocumentEvent.send(e,
						r -> sqsTemplate.convertAndSend(r.getEndpoint(), r.getEvent(), auth)));
	}

	@Override
	public void riskEngineResultEventV2Notification(CreditsV3Entity event) {
		Option.of(event)
				.peek(e -> riskEngineResultEventv2Event.send(e,
						r -> sqsTemplate.convertAndSend(r.getEndpoint(), r.getEvent())));
	}
}
