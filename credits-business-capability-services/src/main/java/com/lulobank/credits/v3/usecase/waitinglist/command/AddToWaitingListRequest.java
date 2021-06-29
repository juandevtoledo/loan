package com.lulobank.credits.v3.usecase.waitinglist.command;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddToWaitingListRequest {
	
	private String idClient;
	private String idProductOffer;
	private Map<String,String> auth;

}
