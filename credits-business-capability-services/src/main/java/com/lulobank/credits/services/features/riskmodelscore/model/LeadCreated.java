package com.lulobank.credits.services.features.riskmodelscore.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LeadCreated {
    private String idCredit;
    private List<LeadCondition> leadConditions;
}
