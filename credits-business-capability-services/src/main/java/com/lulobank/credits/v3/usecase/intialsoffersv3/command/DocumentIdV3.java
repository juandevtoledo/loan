package com.lulobank.credits.v3.usecase.intialsoffersv3.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DocumentIdV3 {
    private final String id;
    private final String type;
    private final String issueDate;
    private final String expirationDate;
}