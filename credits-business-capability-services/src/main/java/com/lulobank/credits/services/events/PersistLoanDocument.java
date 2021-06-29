package com.lulobank.credits.services.events;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PersistLoanDocument {
    private String idClient;
    private String idCredit;
    private String idCbs;
    private String idCard;
	private String accountId;
}
