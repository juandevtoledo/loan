package com.lulobank.credits.services.features.onboardingvalidations;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.credits.sdk.dto.ContentResponse;
import com.lulobank.credits.sdk.dto.ResponsePendingValidations;
import com.lulobank.credits.services.domain.StateBlackList;
import com.lulobank.credits.services.exceptions.ValidateRequestException;
import com.lulobank.credits.services.features.services.PendingValidationsService;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanForClient;
import com.lulobank.credits.services.utils.CreditsErrorResultEnum;
import org.apache.commons.lang3.Validate;

public class PendingValidationsValidator implements Validator<CreateLoanForClient> {

    public static final String NEED_EMAIL_VERIFICATION = "need email verification";
    public static final String NEED_ADMIN_VALIDATION = "need admin validation";
    public static final String THE_TRANSACTION_IS_PROGRESS = "the transaction is progress";
    public static final String EMAIL = "EMAIL";
    public static final String ADMIN_VALIDATION = "ADMIN_VALIDATION";
    public static final String PROCESSING_TRANSACTION = "PROCESSING_TRANSACTION";

    PendingValidationsService pendingValidationsService;

    private ValidationResult validationResult;

    public PendingValidationsValidator(PendingValidationsService pendingValidationsService) {
        this.pendingValidationsService = pendingValidationsService;
    }

    @Override
    public ValidationResult validate(CreateLoanForClient createLoanClientRequest) {
        ResponsePendingValidations responsePendingValidations = this.pendingValidationsService.getPendingValidations(createLoanClientRequest.getIdClient());
        try {
            validateClientExist(responsePendingValidations.getContent());
            validateBlackList(responsePendingValidations.getContent().getBlackListState());
            validateEmailVerified(responsePendingValidations.getContent());
            Validate.notNull(createLoanClientRequest.getLoanConditionsList());
        }catch(ValidateRequestException e){
            return validationResult;
        }
        return null;
    }

    private void validateEmailVerified(ContentResponse contentResponse) {
        if (Boolean.FALSE.equals(contentResponse.getEmailVerified())) {
            validationResult = new ValidationResult(NEED_EMAIL_VERIFICATION, EMAIL);
            throw new ValidateRequestException();
        }
    }

    private void validateClientExist(ContentResponse contentResponse) {
        if(Boolean.FALSE.equals(contentResponse.getClientExists())){
            validationResult = new ValidationResult(CreditsErrorResultEnum.CLIENT_NOT_EXIST.name(), CreditsErrorResultEnum.CLIENT_NOT_EXIST.name());
            throw new ValidateRequestException();
        }
    }

    private void validateBlackList(String blackListState) {
        switch (StateBlackList.valueOf(blackListState)){
            case BLACKLISTED:
                validationResult = new ValidationResult(NEED_ADMIN_VALIDATION, ADMIN_VALIDATION);
                throw new ValidateRequestException();
            case WAITING_FOR_VERIFICATION:
                validationResult = new ValidationResult(THE_TRANSACTION_IS_PROGRESS, PROCESSING_TRANSACTION);
                throw new ValidateRequestException();
            default:
                validationResult = null;
        }
    }

}
