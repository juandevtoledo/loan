package com.lulobank.credits.v3.port.in.approvedriskengine;

import java.math.BigDecimal;
import java.util.UUID;

import com.lulobank.credits.v3.dto.CreditType;
import com.lulobank.credits.v3.dto.LoanStatusV3;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResultEventV2Message.RiskResult;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferNotificationService;
import com.lulobank.credits.v3.port.out.productoffer.dto.CreateProductOfferNotificationRequest;
import com.lulobank.credits.v3.service.PreApprovedClientOfferService;
import com.lulobank.credits.v3.service.RiskEngineResultValidationsService;
import com.lulobank.credits.v3.util.UseCase;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

@CustomLog
@AllArgsConstructor
public class RiskEngineResultEventV2UseCase implements UseCase<RiskEngineResultEventV2Message, Try<Void>> {

	private static final String CONFIRM_PRE_APPROVED_TYPE = "CONFIRM_PREAPPROVED";
    
	private final CreditsV3Repository creditsV3Repository;
	private final ProductOfferNotificationService productOfferNotificationService;
	private final RiskEngineResultValidationsService riskEngineResultValidationsService;
	private final PreApprovedClientOfferService clientOfferService;

    @Override
    public Try<Void> execute(RiskEngineResultEventV2Message riskEngineResultEventV2Message) {
    	
    	return riskEngineResultValidationsService.isEventCompleted(riskEngineResultEventV2Message.getStatus())
		    	.flatMap(ignored -> riskEngineResultValidationsService.clientDoesntHaveActiveCredit(riskEngineResultEventV2Message.getIdClient()))
		    	.flatMap(ignored -> getRiskResult(riskEngineResultEventV2Message))
		    	.flatMap(result -> riskEngineResultValidationsService.isPreApprovedCredit(result.getApproved())
		    			.flatMap(ignored -> riskEngineResultValidationsService.amountIsBiggerThanMinLoanAmount(result.getLoanAmount()))
		    			.flatMap(ignored -> clientOfferService.closePreApprovedClientOffers(riskEngineResultEventV2Message.getIdClient())
		    					.toEither("Error closing pre approved client offers"))
		    			.flatMap(ignored -> saveCredit(riskEngineResultEventV2Message, result)))
		    	.fold(error -> createFailfedCreditEntity(error, riskEngineResultEventV2Message),
						ignored -> Try.run(() -> log.info("[RiskEngineResultEventV2UseCase] Execution has finished successful")));
    }

	private Either<String, Void> saveCredit(RiskEngineResultEventV2Message riskEngineResultEventV2Message, RiskResult result) {
		return creditsV3Repository.save(RiskEngineResultEventV2Mapper.INSTANCE.toCreditsV3Entity(riskEngineResultEventV2Message))
				.flatMap(creditsV3Entity -> createProductOffer(riskEngineResultEventV2Message, result.getLoanAmount()))
				.onFailure(error -> log.error(String.format("Error saving event from motor Logos. idClient: %s",
						riskEngineResultEventV2Message.getIdClient()), error))
				.toEither("Error saving event from motor Logos");
	}
    
    private Try<Void> createFailfedCreditEntity(String errorMessage, RiskEngineResultEventV2Message riskEngineResultEventV2Message) {
    	return Try.run(() -> {
    		log.error(String.format("[RiskEngineResultEventV2UseCase] Execution has finished with errors : %s", errorMessage));
			creditsV3Repository.save(buildFailedCreditsV3Entity(riskEngineResultEventV2Message, errorMessage));
    	});
    }

	private CreditsV3Entity buildFailedCreditsV3Entity(RiskEngineResultEventV2Message riskEngineResultEventV2Message,
			String ErrorMessage) {
		LoanStatusV3 loanStatusV3 = new LoanStatusV3();
		loanStatusV3.setStatus("FAILED");
		CreditsV3Entity creditsV3Entity = new CreditsV3Entity();
		creditsV3Entity.setIdCredit(UUID.randomUUID());
		creditsV3Entity.setIdClient(riskEngineResultEventV2Message.getIdClient());
		creditsV3Entity.setRiskEngineDescription(ErrorMessage);
		creditsV3Entity.setRiskEngineDetail(riskEngineResultEventV2Message.toString());
		creditsV3Entity.setCreditType(CreditType.PREAPPROVED);
		creditsV3Entity.setStatus(riskEngineResultEventV2Message.getStatus());
		creditsV3Entity.setLoanStatus(loanStatusV3);
		return creditsV3Entity;
	}
    
    private Either<String, RiskResult> getRiskResult(RiskEngineResultEventV2Message riskEngineResultEventV2Message) {
    	return Option.ofOptional(riskEngineResultEventV2Message.getResults().stream().findFirst())
    			.toEither("It is not possible to get RiskResult from RiskEngineResultEventV2Message");
    }
    
    private Try<Void> createProductOffer(RiskEngineResultEventV2Message riskEngineResultEventV2Message, BigDecimal loanAmount) {
    	return productOfferNotificationService.createProductOffer(buildConfirmPreApprovedNotificationRequest(riskEngineResultEventV2Message, loanAmount));
    }

	private CreateProductOfferNotificationRequest buildConfirmPreApprovedNotificationRequest(
			RiskEngineResultEventV2Message riskEngineResultEventV2Message, BigDecimal loanAmount) {
		return CreateProductOfferNotificationRequest.builder()
				.idClient(riskEngineResultEventV2Message.getIdClient())
				.type(CONFIRM_PRE_APPROVED_TYPE)
				.value(loanAmount.intValue())
				.build();
	}
}