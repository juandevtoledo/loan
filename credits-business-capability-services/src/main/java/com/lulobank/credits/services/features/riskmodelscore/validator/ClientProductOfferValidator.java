package com.lulobank.credits.services.features.riskmodelscore.validator;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.credits.services.features.riskmodelscore.model.ClientProductOffer;
import org.springframework.http.HttpStatus;

import java.util.Objects;

public class ClientProductOfferValidator implements Validator<ClientProductOffer> {
    public static final String ID_CLIENT_INVALID = "has a wrong format";
    @Override
    public ValidationResult validate(ClientProductOffer clientProductOffer) {
        if (Objects.isNull(clientProductOffer.getIdClient())){
            return new ValidationResult(ID_CLIENT_INVALID, String.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
        return null;
    }
}
