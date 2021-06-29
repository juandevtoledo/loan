package com.lulobank.credits.v3.service;

import static com.lulobank.credits.v3.dto.CreditType.PREAPPROVED;
import static com.lulobank.credits.v3.port.in.loan.LoanState.CLOSED;

import java.util.Objects;

import com.lulobank.credits.v3.dto.LoanStatusV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

@CustomLog
@AllArgsConstructor
public class PreApprovedClientOfferService {
	
	private final CreditsV3Repository creditsV3Repository;

	public Try<String> closePreApprovedClientOffers(String idClient){
		log.info("[ClientOfferService] closeClientOffers()");
		
		return creditsV3Repository.findByIdClient(idClient)
			.filter(this::isPreapprovedLoan)
			.flatMap(this::updateCreditsV3Entity)
			.map(ignored -> idClient)
			.toTry()
			.recover(error -> {
				log.info("[ClientOfferService] No credit found");
				return idClient;
			});
	}
	
	private Try<CreditsV3Entity> updateCreditsV3Entity(CreditsV3Entity creditsV3Entity) {
		return creditsV3Repository.save(setLoanStatus(creditsV3Entity)).toTry();
	}
	
	private CreditsV3Entity setLoanStatus(CreditsV3Entity creditsV3Entity) {
		LoanStatusV3 loanStatus = new LoanStatusV3();
		loanStatus.setStatus(CLOSED.name());
		creditsV3Entity.setLoanStatus(loanStatus);
		return creditsV3Entity;
	}
	
	private boolean isPreapprovedLoan(CreditsV3Entity creditsV3Entity) {
        return PREAPPROVED.equals(creditsV3Entity.getCreditType())
                && Objects.nonNull(creditsV3Entity.getInitialOffer())
                && Objects.nonNull(creditsV3Entity.getInitialOffer().getResults())
                && Objects.isNull(creditsV3Entity.getLoanStatus());
    }
}
