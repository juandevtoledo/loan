package com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionRequest {
	private final String idClient;
	private final int dayOfPay;
	private final String metadata;
}
