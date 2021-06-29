package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentIdV3 {
    private String id;
    private String type;
    private String issueDate;
    private String expirationDate;
}
