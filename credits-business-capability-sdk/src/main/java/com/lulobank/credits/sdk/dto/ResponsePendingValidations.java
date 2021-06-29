package com.lulobank.credits.sdk.dto;

import com.lulobank.core.validations.ValidationResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePendingValidations {

    private ContentResponse content;
    List<ValidationResult> errors;
    private Boolean hasErrors;
}
