package com.lulobank.credits.services.outboundadapters.flexibility;

import com.lulobank.credits.sdk.dto.loandetails.NextInstallment;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityInstallmentPendingStateEnum;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum;
import flexibility.client.models.response.GetLoanResponse;
import org.apache.commons.lang3.EnumUtils;

import java.util.Comparator;
import java.util.Objects;

public class FlexibilityLoanServices {

    private GetLoanResponse getLoanResponse;

    public FlexibilityLoanServices(GetLoanResponse getLoanResponse) {
        this.getLoanResponse = getLoanResponse;
    }

    public  Integer countPaidInstallments(){
        if(!Objects.isNull(getLoanResponse)) {
            Long count = getLoanResponse.getPaymentPlanItemApiList().stream().
                    filter(x -> x.getState().equals(CbsLoanStateEnum.PAID.name())).count();
            return count.intValue();
        }
        return 0;
    }

    public NextInstallment getNextInstallment(){
        if(!Objects.isNull(getLoanResponse)) {
            GetLoanResponse.PaymentPlanItem nextPaymentPlanItem = getLoanResponse.getPaymentPlanItemApiList().stream().
                    sorted(Comparator.comparing(GetLoanResponse.PaymentPlanItem::getDueDate)).
                    filter(x->EnumUtils.isValidEnum(FlexibilityInstallmentPendingStateEnum.class,x.getState())).
                    findFirst().orElse(null);

            return nextPaymentPlanItem == null ? null : new NextInstallment(nextPaymentPlanItem.getDueDate().toString(), nextPaymentPlanItem.getTotalDue());
        }
        return null;
    }
}
