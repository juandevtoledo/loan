package com.lulobank.credits.services.utils;

import com.lulobank.core.validations.ValidationResult;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class UtilValidators {

    private UtilValidators() {
    }

    public static List<ValidationResult> getListValidations(String errorMessage, String errorCode) {
        List<ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(new ValidationResult(errorMessage, errorCode));
        return validationResults;
    }

    public static HttpStatus getHttpStatusByCode(String code) {
        try {
            return HttpStatus.resolve(Integer.parseInt(code));
        } catch (NumberFormatException e) {
            return HttpStatus.NOT_ACCEPTABLE;
        }
    }
}
