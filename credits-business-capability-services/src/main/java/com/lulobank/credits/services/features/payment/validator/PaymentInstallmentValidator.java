package com.lulobank.credits.services.features.payment.validator;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.credits.sdk.dto.payment.PaymentInstallment;
import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.UUID;

import static com.lulobank.core.utils.ValidatorUtils.getNotNullValidations;

public class PaymentInstallmentValidator implements Validator<PaymentInstallment> {

    @Override
    public ValidationResult validate(PaymentInstallment request) {
        ValidationResult result;
        result = getNotNullValidations(request);
        if (Objects.nonNull(result)) {
            return result;
        }
        return validateUUIDFields(request);
    }

    private ValidationResult validateUUIDFields(PaymentInstallment request) {
        try {
            UUID.fromString(request.getIdCredit());
            UUID.fromString(request.getIdClient());
            return null;
        } catch (IllegalArgumentException e) {
            return new ValidationResult("UUID field wrong format", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
    }
}
