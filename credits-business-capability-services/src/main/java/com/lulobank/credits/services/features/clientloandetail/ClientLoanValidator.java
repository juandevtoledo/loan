package com.lulobank.credits.services.features.clientloandetail;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.credits.sdk.dto.clientloandetail.GetClientLoan;

import java.util.Objects;

import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;
import static java.util.UUID.fromString;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class ClientLoanValidator implements Validator<GetClientLoan> {

    @Override
    public ValidationResult validate(GetClientLoan getClientLoan) {
        ValidationResult validationResult =  getNotNullValidations(getClientLoan);
        if (Objects.nonNull(validationResult))
            return validationResult;
        try {
            fromString(getClientLoan.getIdClient());
        }catch (IllegalArgumentException ex){
            return new ValidationResult("idClient is invalid",String.valueOf(BAD_REQUEST.value()));
        }
        return null;
    }
}
