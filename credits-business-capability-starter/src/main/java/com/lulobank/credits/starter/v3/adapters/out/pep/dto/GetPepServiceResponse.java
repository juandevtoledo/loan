package com.lulobank.credits.starter.v3.adapters.out.pep.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPepServiceResponse {
	private String pep;
}
