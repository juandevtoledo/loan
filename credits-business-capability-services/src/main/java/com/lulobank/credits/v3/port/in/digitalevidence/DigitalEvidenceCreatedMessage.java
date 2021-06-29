package com.lulobank.credits.v3.port.in.digitalevidence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DigitalEvidenceCreatedMessage {
	
	private String idClient;
	private String idCredit;
	private String idCbs;
	private String accountId;
	private boolean success;
}
