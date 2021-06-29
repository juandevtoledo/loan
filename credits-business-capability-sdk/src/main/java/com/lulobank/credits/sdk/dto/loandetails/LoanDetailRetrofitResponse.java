package com.lulobank.credits.sdk.dto.loandetails;

import com.lulobank.core.validations.ValidationResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoanDetailRetrofitResponse {
    private List<LoanDetail> content;
    List<ValidationResult> errors;
    private Boolean hasErrors;
}
