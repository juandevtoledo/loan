
package com.lulobank.credits.sdk.dto;

import com.lulobank.core.validations.ValidationResult;
import lombok.Getter;

import java.util.List;

@Getter
public class CreditErrorResult implements CreditResult {
    private List<ValidationResult> errors;
    public CreditErrorResult(List<ValidationResult> errors) {
        this.errors = errors;
    }

}
