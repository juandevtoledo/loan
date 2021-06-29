package com.lulobank.credits.v3.port.out.pep;

import java.util.Map;

import com.lulobank.credits.v3.port.out.pep.dto.GetPepResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;

public interface PepService {

	Either<UseCaseResponseError, GetPepResponse> getPep(String idClient, Map<String,String> auth);
}
