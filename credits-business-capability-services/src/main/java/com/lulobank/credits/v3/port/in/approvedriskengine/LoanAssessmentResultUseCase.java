package com.lulobank.credits.v3.port.in.approvedriskengine;

import java.math.BigDecimal;
import java.util.UUID;

import com.lulobank.credits.v3.dto.CreditType;
import com.lulobank.credits.v3.dto.LoanStatusV3;
import com.lulobank.credits.v3.port.in.approvedriskengine.LoanAssessmentResultMessage.RiskResult;
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
public class LoanAssessmentResultUseCase implements UseCase<LoanAssessmentResultMessage, Try<Void>> {
	
	private static final String CONFIRM_PRE_APPROVED_TYPE = "CONFIRM_PREAPPROVED";
	private static final String REGISTRY_PRE_APPROVED_TYPE = "REGISTRY_PREAPPROVED";
	
	private final CreditsV3Repository creditsV3Repository;
    private final ProductOfferNotificationService productOfferNotificationService;
    private final RiskEngineResultValidationsService riskEngineResultValidationsService;
    private final PreApprovedClientOfferService clientOfferService;

    @Override
    public Try<Void> execute(LoanAssessmentResultMessage loanAssessmentResultMessage) {
    	
    	return riskEngineResultValidationsService.isEventCompleted(loanAssessmentResultMessage.getStatus())
    	    	.flatMap(ignored -> riskEngineResultValidationsService.clientDoesntHaveActiveCredit(loanAssessmentResultMessage.getIdClient()))
    	    	.flatMap(ignored -> getRiskResult(loanAssessmentResultMessage))
    	    	.flatMap(result -> riskEngineResultValidationsService.isPreApprovedCredit(result.getApproved())
    	    			.flatMap(ignored -> riskEngineResultValidationsService.amountIsBiggerThanMinLoanAmount(result.getLoanAmount()))
    	    			.flatMap(ignored -> clientOfferService.closePreApprovedClientOffers(loanAssessmentResultMessage.getIdClient())
    	    					.toEither("Error closing pre approved client offers"))
    	    			.flatMap(ignored -> saveCredit(loanAssessmentResultMessage, result)))
    	    	.fold(error -> createDefaultProductOffer(error, loanAssessmentResultMessage),
    					ignored -> Try.run(() -> log.info("[LoanAssessmentResultUseCase] Execution has finished successful")));
    }

	private Either<String, Void> saveCredit(LoanAssessmentResultMessage loanAssessmentResultMessage, RiskResult result) {
		return creditsV3Repository.save(LoanAssessmentResultMapper.INSTANCE.toCreditsV3Entity(loanAssessmentResultMessage))
				.flatMap(creditsV3Entity -> createProductOffer(
						buildConfirmPreApprovedNotificationRequest(loanAssessmentResultMessage, result.getLoanAmount())))
				.onFailure(error -> log.error(String.format("Error saving event from motor Logos. idClient: %s",
						loanAssessmentResultMessage.getIdClient()), error))
				.toEither("Error saving event from motor Logos");
	}
    
    private Try<Void> createDefaultProductOffer(String errorMessage, LoanAssessmentResultMessage loanAssessmentResultMessage) {
    	return Try.run(() -> {
    		log.error(String.format("[LoanAssessmentResultUseCase] Execution has finished with errors : %s", errorMessage));
			creditsV3Repository.save(createFailedCreditsV3Entity(loanAssessmentResultMessage, errorMessage));
			createProductOffer(buildRegistryPreApprovedNotificationRequest(loanAssessmentResultMessage));
    	});
    }

	private CreditsV3Entity createFailedCreditsV3Entity(LoanAssessmentResultMessage loanAssessmentResultMessage,
			String ErrorMessage) {
		LoanStatusV3 loanStatusV3 = new LoanStatusV3();
		loanStatusV3.setStatus("FAILED");
		CreditsV3Entity creditsV3Entity = new CreditsV3Entity();
		creditsV3Entity.setIdCredit(UUID.randomUUID());
		creditsV3Entity.setIdClient(loanAssessmentResultMessage.getIdClient());
		creditsV3Entity.setRiskEngineDescription(ErrorMessage);
		creditsV3Entity.setRiskEngineDetail(loanAssessmentResultMessage.toString());
		creditsV3Entity.setCreditType(CreditType.PREAPPROVED);
		creditsV3Entity.setStatus(loanAssessmentResultMessage.getStatus());
		creditsV3Entity.setLoanStatus(loanStatusV3);
		return creditsV3Entity;
	}

	private Either<String, RiskResult> getRiskResult(LoanAssessmentResultMessage loanAssessmentResultMessage) {
    	return Option.ofOptional(loanAssessmentResultMessage.getResults().stream().findFirst())
    			.toEither("It is not possible to get RiskResult from LoanAssessmentResultUseCase");
    }
    
    private Try<Void> createProductOffer(CreateProductOfferNotificationRequest createProductOfferNotificationRequest) {
    	return productOfferNotificationService.createProductOffer(createProductOfferNotificationRequest);
    }

	private CreateProductOfferNotificationRequest buildConfirmPreApprovedNotificationRequest(
			LoanAssessmentResultMessage loanAssessmentResultMessage, BigDecimal maxTotalAmount) {
		return CreateProductOfferNotificationRequest.builder()
				.idClient(loanAssessmentResultMessage.getIdClient())
				.type(CONFIRM_PRE_APPROVED_TYPE)
				.value(maxTotalAmount.intValue())
				.build();
	}
	
	private CreateProductOfferNotificationRequest buildRegistryPreApprovedNotificationRequest(
			LoanAssessmentResultMessage loanAssessmentResultMessage) {
		return CreateProductOfferNotificationRequest.builder()
				.idClient(loanAssessmentResultMessage.getIdClient())
				.type(REGISTRY_PRE_APPROVED_TYPE)
				.build();
	}
}