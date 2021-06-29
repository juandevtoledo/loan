package com.lulobank.credits.v3.usecase.intialsoffersv3.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhoneV3 {
    private final String number;
    private final String prefix;
}