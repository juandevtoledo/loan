package com.lulobank.credits.starter.v3.adapters.out.schedule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateTransactionMessage {

	private final String idClient;
	private final String metadata;
	private final int executionDay;
	private final ScheduleType type;
	private final ReplyTo replyTo;
	private final TransactionEvent transactionEvent;

	@Getter
	@Builder
	public static class ReplyTo {
		private final String sqs;
	}
	
	@Getter
	@Builder
	public static class TransactionEvent {
		private final String eventType;
		private final Payload payload;
	}
	
	@Getter
	@Builder
	public static class Payload {
		private final String idCredit;
	}
}
