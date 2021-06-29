package com.lulobank.credits.starter.v3.adapters.in.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public abstract class AdapterResponse {
}
