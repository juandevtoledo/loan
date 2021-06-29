package com.lulobank.credits.v3.port.in.nextinstallment;

import com.lulobank.credits.v3.port.in.nextinstallment.dto.NextInstallment;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;

public interface GenerateNextInstallmentPort extends UseCase<String, Either<UseCaseResponseError, NextInstallment>> {
}
