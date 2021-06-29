package com.lulobank.credits.starter.v3.mocks;

import com.lulobank.credits.v3.port.in.nextinstallment.GenerateNextInstallmentPort;
import com.lulobank.credits.v3.port.in.nextinstallment.dto.Flag;
import com.lulobank.credits.v3.port.in.nextinstallment.dto.NextInstallment;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import com.lulobank.credits.v3.vo.loan.AmountVO;
import com.lulobank.credits.v3.vo.loan.CurrencyVO;
import com.lulobank.credits.v3.vo.loan.Money;
import io.vavr.control.Either;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.lulobank.credits.v3.usecase.nextinstallment.InstallmentState.UP_TO_DAY;

public class GenerateNextInstallmentUseCaseMock implements GenerateNextInstallmentPort {

    @Override
    public Either<UseCaseResponseError, NextInstallment> execute(String command) {
        return Either.right(NextInstallment.builder()
                .flags(getFlags())
                .idCredit("0467b237-925a-40d6-aa57-7dcdce68681f")
                .idLoanCBS("79624120546")
                .installmentDate(LocalDateTime.now())
                .state("ACTIVE")
                .installmentState(UP_TO_DAY)
                .nextInstallmentAmount(buildMoney(BigDecimal.valueOf(28955.55)))
                .payOffAmount(buildMoney(BigDecimal.valueOf(1348955.55)))
                .requestedAmount(buildMoney(BigDecimal.valueOf(400000.0)))
                .build());
    }

    private Flag getFlags() {
        return Flag.builder()
                .automaticDebitActive(true)
                .minimumPaymentActive(true)
                .build();
    }

    private Money buildMoney(BigDecimal amount) {
        return Money
                .builder()
                .amount(AmountVO.builder().value(amount).build())
                .currency(CurrencyVO.builder().value("COP").build())
                .build();
    }
}
