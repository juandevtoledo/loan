package com.lulobank.credits.starter.v3.adapters.out.savingsaccounts.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SavingAccountType {

	private Content content;

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Content {
		private String idSavingAccount;
		private String state;
		private String type;
		private Boolean simpleDeposit;
		private Boolean gmf;
		private SavingAccountBalance balance;
		private String creationDate;
	}

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class SavingAccountBalance {
		private BigDecimal amount;
		private String currency;
		private BigDecimal availableAmount;
	}
}
