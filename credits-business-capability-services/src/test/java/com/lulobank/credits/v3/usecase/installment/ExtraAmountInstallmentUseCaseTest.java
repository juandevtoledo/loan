package com.lulobank.credits.v3.usecase.installment;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.AmountCurrency;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.usecase.installment.command.CalculateExtraAmountInstallment;
import com.lulobank.credits.v3.usecase.installment.dto.ExtraAmountInstallmentResult;
import com.lulobank.credits.v3.usecase.installment.util.ExtraAmountType;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ExtraAmountInstallmentUseCaseTest {

    @Mock
    private CoreBankingService coreBankingService;
    @Mock
    private CreditsV3Repository creditsV3Repository;

    private ExtraAmountInstallmentUseCase testClass;

    private CalculateExtraAmountInstallment calculateExtraAmountInstallment;

    private final String ID_CREDIT = "657d1417-71f1-4bb2-a7ed-35edbad94523";
    private final String ID_CLIENT_MAMBU = "1999982388";
    private final String ID_LOAN_ACCOUNT_MAMBU = "1999982388";
    private final BigDecimal AMOUNT = BigDecimal.valueOf(5000);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testClass = new ExtraAmountInstallmentUseCase(coreBankingService, creditsV3Repository);
        calculateExtraAmountInstallment = CalculateExtraAmountInstallment
                .builder()
                .idCredit(ID_CREDIT)
                .amount(AMOUNT)
                .build();

    }

    @Test
    public void creditNotFountInDB() {
        when(creditsV3Repository.findById(ID_CREDIT)).thenReturn(Option.none());

        Either<UseCaseResponseError, ExtraAmountInstallmentResult> result = testClass.execute(calculateExtraAmountInstallment);

        assertTrue(result.isLeft());
        assertFalse(result.isRight());
        assertEquals("404", result.getLeft().getProviderCode());
        assertEquals("D", result.getLeft().getDetail());
        assertEquals("CRE_101", result.getLeft().getBusinessCode());

    }

    @Test
    public void creditFountButErrorCoreBanking() {
        when(creditsV3Repository.findById(ID_CREDIT)).thenReturn(Option.of(buildCreditEntity()));
        when(coreBankingService.getLoanInformation( eq(ID_LOAN_ACCOUNT_MAMBU),eq(ID_CLIENT_MAMBU))).thenReturn(Either.left(CoreBankingError.getParametersError()));

        Either<UseCaseResponseError, ExtraAmountInstallmentResult> result = testClass.execute(calculateExtraAmountInstallment);

        assertTrue(result.isLeft());
        assertFalse(result.isRight());
        assertEquals("502", result.getLeft().getProviderCode());
        assertEquals("P_CB", result.getLeft().getDetail());
        assertEquals("CRE_103", result.getLeft().getBusinessCode());

    }

    @Test
    public void ExtraAmountType_WhenLoanExist() {
        when(creditsV3Repository.findById(ID_CREDIT)).thenReturn(Option.of(buildCreditEntity()));
        when(coreBankingService.getLoanInformation( eq(ID_LOAN_ACCOUNT_MAMBU),eq(ID_CLIENT_MAMBU))).thenReturn(Either.right(buildCoreBankingObject()));
        Either<UseCaseResponseError, ExtraAmountInstallmentResult> result = testClass.execute(calculateExtraAmountInstallment);
        assertTrue(result.isRight());
        assertThat(result.get().getPaymentType(),is("MINIMUM_PAYMENT"));
    }

    private CreditsV3Entity buildCreditEntity() {
        CreditsV3Entity creditsV3Entity = new CreditsV3Entity();
        creditsV3Entity.setIdClientMambu(ID_CLIENT_MAMBU);
        creditsV3Entity.setIdLoanAccountMambu(ID_LOAN_ACCOUNT_MAMBU);
        return creditsV3Entity;
    }

    private LoanInformation buildCoreBankingObject() {
        return LoanInformation.builder()
                .totalBalance(AmountCurrency.builder().value(BigDecimal.valueOf(500000)).build())
                .installmentExpected(AmountCurrency.builder().value(BigDecimal.valueOf(20000)).build())
                .build();
    }

}
