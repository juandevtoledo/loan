package com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper;

import com.lulobank.credits.v3.port.out.corebanking.dto.ClientAccount;
import flexibility.client.models.request.GetAccountRequest;
import flexibility.client.models.response.GetAccountResponse;
import flexibility.client.util.GetAccountRequestBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class AccountsMapper {

    public static GetAccountRequest accountRequestTo(String clientId) {
        return GetAccountRequestBuilder.aGetAccountRequest()
                .withClientId(clientId)
                .build();
    }

    public static List<ClientAccount> accountRequestTo(List<GetAccountResponse> accountResponseList) {
        return accountResponseList.stream()
                .map(getAccountResponse ->
                        ClientAccount.builder()
                                .balance(BigDecimal.valueOf(getAccountResponse.getBalance().getAmount()))
                                .number(getAccountResponse.getNumber())
                                .status(getAccountResponse.getState())
                                .gmf(Boolean.parseBoolean(getAccountResponse.getGmf()))
                                .build()
                ).collect(Collectors.toList());
    }
}
