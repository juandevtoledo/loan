package com.lulobank.credits.v3.port.in.savingsaccount;

import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;

import co.com.lulobank.tracing.restTemplate.HttpError;
import io.vavr.control.Either;

import java.util.Map;


public interface SavingsAccountV3Service {

    Either<HttpError, SavingsAccountResponse> create(SavingsAccountRequest createSavingsAccountEntity,Map<String, String> auth);
}
