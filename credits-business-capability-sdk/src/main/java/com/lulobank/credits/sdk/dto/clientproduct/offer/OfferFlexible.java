package com.lulobank.credits.sdk.dto.clientproduct.offer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class OfferFlexible extends Offer{
   private List<FlexibleLoanSimulationInstallments> simulateInstallment;
}
