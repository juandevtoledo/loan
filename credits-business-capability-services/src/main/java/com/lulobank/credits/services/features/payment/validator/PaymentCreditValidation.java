package com.lulobank.credits.services.features.payment.validator;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.credits.services.features.payment.model.DoPaymentCredit;
import com.lulobank.credits.services.utils.CreditsErrorResultEnum;
import org.apache.commons.lang3.Validate;
public class PaymentCreditValidation implements Validator<DoPaymentCredit> {
    @Override
    public ValidationResult validate(DoPaymentCredit doPaymentCredit) {
        try{
            Validate.notNull(doPaymentCredit);
            Validate.notNull(doPaymentCredit.getIdCredit());
            Validate.notNull(doPaymentCredit.getAmount());
        }
        catch (NullPointerException e){
            return new ValidationResult(CreditsErrorResultEnum.VALIDATION_ERROR.name(), "Incomplete information to pay the credit");
        }
        return null;
    }
}