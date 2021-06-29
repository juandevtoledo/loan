package com.lulobank.credits.starter.v3.adapters.in.sqs.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanStatementsScheduler {
    private String message;
}