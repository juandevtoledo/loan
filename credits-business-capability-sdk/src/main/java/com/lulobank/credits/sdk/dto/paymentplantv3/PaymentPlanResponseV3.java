package com.lulobank.credits.sdk.dto.paymentplantv3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class PaymentPlanResponseV3 {
    private List<PaymentV3> paymentPlan;
}
