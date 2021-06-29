package com.lulobank.credits.v3.port.out.saving;

import java.util.Map;

import com.lulobank.credits.v3.port.out.saving.dto.GetSavingAcountTypeResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;

public interface SavingAccountService {
	
	Either<UseCaseResponseError, GetSavingAcountTypeResponse> getSavingAccount(String idClient, Map<String,String> auth);
}
