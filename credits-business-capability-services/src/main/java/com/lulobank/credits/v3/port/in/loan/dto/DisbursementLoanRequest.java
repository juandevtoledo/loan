package com.lulobank.credits.v3.port.in.loan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisbursementLoanRequest {
	private String idClient;
	private String idClientMambu;
	private String idCreditMambu;
}
