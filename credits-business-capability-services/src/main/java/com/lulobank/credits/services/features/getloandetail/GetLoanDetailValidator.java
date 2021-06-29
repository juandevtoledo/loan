package com.lulobank.credits.services.features.getloandetail;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;

import java.util.Objects;

public class GetLoanDetailValidator implements Validator<GetLoanDetail> {

    public static final String ID_CLIENT_INVALID = "has a wrong format";

    @Override
    public ValidationResult validate(GetLoanDetail getLoanDetail) {
        if (Objects.isNull(getLoanDetail.getIdClient())){
            return new ValidationResult(ID_CLIENT_INVALID,"idClient");
        }
        return null;
    }
}
