package com.lulobank.credits.sdk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentResponse {

    private String blackListState;
    private Boolean emailVerified;
    private Boolean clientExists;
}
