package com.lulobank.credits.sdk.dto.clientloandetail;

import java.util.stream.Stream;

import io.vavr.control.Option;

public enum LoanStateHomologate {
	PENDING_APPROVAL("PENDING"),
	APPROVED("PENDING");
	
	private String homologateTo;
	
	LoanStateHomologate(String homologateTo) {
		this.homologateTo = homologateTo;
	}
	
	public static String getStatusHomologate(String state) {
		return Option.ofOptional(Stream.of(LoanStateHomologate.values())
				.filter(loanstate -> loanstate.name().equals(state))
				.findFirst())
			.fold(() -> state, homologateState -> homologateState.homologateTo);

	}
}
