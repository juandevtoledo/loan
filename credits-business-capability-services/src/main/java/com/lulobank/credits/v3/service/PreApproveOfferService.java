package com.lulobank.credits.v3.service;

import java.util.List;

import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.usecase.acceptoffer.command.AcceptOfferCommand;

import io.vavr.control.Option;
import lombok.CustomLog;

@CustomLog
public class PreApproveOfferService {

	public Option<OfferEntityV3> getOffer(CreditsV3Entity creditsV3Entities, AcceptOfferCommand acceptOfferCommand) {

		return findOfferEntity(creditsV3Entities.getInitialOffer().getOfferEntities(), acceptOfferCommand.getIdOffer())
				.flatMap(offerEntityV3 -> mapOfferAditionalInformation(offerEntityV3, acceptOfferCommand));
	}

	private Option<OfferEntityV3> mapOfferAditionalInformation(OfferEntityV3 offerEntity,
			AcceptOfferCommand acceptOfferCommand) {
		return findFlexibleLoan(offerEntity.getFlexibleLoans(), acceptOfferCommand.getInstallment())
				.map(flexibleLoan -> setOfferAditionalInformation(offerEntity, flexibleLoan));
	}

	private OfferEntityV3 setOfferAditionalInformation(OfferEntityV3 offerEntity, FlexibleLoanV3 flexibleLoan) {
		offerEntity.setInterestRate(flexibleLoan.getInterestRate());
		offerEntity.setInstallments(flexibleLoan.getInstallment());
		offerEntity.setMonthlyNominalRate(flexibleLoan.getMonthlyNominalRate());
		offerEntity.setAnnualNominalRate(flexibleLoan.getAnnualNominalRate());
		return offerEntity;
	}

	private Option<FlexibleLoanV3> findFlexibleLoan(List<FlexibleLoanV3> flexibleLoanList, int installment) {
		return Option
				.ofOptional(flexibleLoanList.stream()
						.filter(flexibleLoan -> flexibleLoan.getInstallment().equals(installment)).findFirst())
				.onEmpty(() -> log.error("[PreApproveOfferService] installment not found!"));
	}

	private Option<OfferEntityV3> findOfferEntity(List<OfferEntityV3> list, String idOffer) {
		return Option
				.ofOptional(
						list.stream().filter(offerEntityV3 -> offerEntityV3.getIdOffer().equals(idOffer)).findFirst())
				.onEmpty(() -> log.error("[PreApproveOfferService] Offer not found!"));
	}
}
