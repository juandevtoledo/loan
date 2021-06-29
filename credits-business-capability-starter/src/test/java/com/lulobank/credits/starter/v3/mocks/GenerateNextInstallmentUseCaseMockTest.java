package com.lulobank.credits.starter.v3.mocks;

import com.lulobank.credits.v3.port.in.nextinstallment.GenerateNextInstallmentPort;
import com.lulobank.credits.v3.port.in.nextinstallment.dto.NextInstallment;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GenerateNextInstallmentUseCaseMockTest {

    GenerateNextInstallmentPort generateNextInstallmentUseCase;

    @Before
    public void setUp() throws Exception {
        generateNextInstallmentUseCase = new GenerateNextInstallmentUseCaseMock();
    }

    @Test
    public void NextInstallmentUse_WhenMockBuildResponseSuccess() {
        Either<UseCaseResponseError, NextInstallment> response = generateNextInstallmentUseCase.execute(UUID.randomUUID().toString());
        assertThat(response.isRight(), is(true));
        assetThatNextInstallment(response.get());

    }

    private void assetThatNextInstallment(NextInstallment nextInstallment) {
        assertThat(nextInstallment.getState(), is("ACTIVE"));
        assertThat(nextInstallment.getFlags().isAutomaticDebitActive(), is(true));
        assertThat(nextInstallment.getFlags().isMinimumPaymentActive(), is(true));
        assertThat(nextInstallment.getFlags().isPayNow(), is(false));
        assertThat(nextInstallment.getRequestedAmount().getAmount().getValue(), is(BigDecimal.valueOf(400000.0)));
        assertThat(nextInstallment.getRequestedAmount().getCurrency().getValue(), is("COP"));
    }
}
