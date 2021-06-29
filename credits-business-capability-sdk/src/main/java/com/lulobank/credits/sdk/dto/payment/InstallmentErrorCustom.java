package com.lulobank.credits.sdk.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InstallmentErrorCustom implements InstallmentPaidResponse{

    private List<CustomError> errors;

}
