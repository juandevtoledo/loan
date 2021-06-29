package com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateTransactionRequest {
	private final String idClient;
	private final String idCredit;
	private final int dayOfPay;
	private final String metadata;
}
