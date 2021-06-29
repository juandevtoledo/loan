package com.lulobank.credits.sdk.dto.paymentplan;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetPaymentPlan implements Command {

    private String idCredit;
    private Integer limit;
    private Integer offset;

    public GetPaymentPlan(String idCredit,Integer limit,Integer offset) {
        this.idCredit = idCredit;
        this.limit = limit;
        this.offset=offset;
    }
}
