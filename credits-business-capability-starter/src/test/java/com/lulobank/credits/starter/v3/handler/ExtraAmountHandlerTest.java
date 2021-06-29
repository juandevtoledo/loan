package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.usecase.installment.ExtraAmountInstallmentUseCase;
import com.lulobank.credits.v3.usecase.installment.command.CalculateExtraAmountInstallment;
import com.lulobank.credits.v3.usecase.installment.dto.ExtraAmountInstallmentResult;
import io.vavr.control.Either;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import static org.mockito.Mockito.when;

public class ExtraAmountHandlerTest {

    @Mock
    private ExtraAmountInstallmentUseCase useCase;

    @Captor
    private ArgumentCaptor<CalculateExtraAmountInstallment> commandCaptor;

    private ExtraAmountHandler testClass;

    private final String ID_CREDIT = "657d1417-71f1-4bb2-a7ed-35edbad94523";
    private final BigDecimal AMOUNT = BigDecimal.valueOf(5000);

    @Before
    public void setup (){
        MockitoAnnotations.initMocks(this);
        testClass =new  ExtraAmountHandler(useCase);
    }

    @Test
    public void useCaseResponseError(){
        when(useCase.execute(commandCaptor.capture())).thenReturn(Either.left(CoreBankingError.getParametersError()));
        ResponseEntity<AdapterResponse> response = testClass.executeUseCase(ID_CREDIT,AMOUNT);
        Assert.assertEquals(502, response.getStatusCode().value());
        Assert.assertEquals(ID_CREDIT, commandCaptor.getValue().getIdCredit());
        Assert.assertEquals(AMOUNT, commandCaptor.getValue().getAmount());

    }

    @Test
    public void useCaseResponseSuccessful(){
        when(useCase.execute(commandCaptor.capture())).thenReturn(Either.right(ExtraAmountInstallmentResult.builder().build()));
        ResponseEntity<AdapterResponse> response = testClass.executeUseCase(ID_CREDIT,AMOUNT);
        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals(ID_CREDIT, commandCaptor.getValue().getIdCredit());
        Assert.assertEquals(AMOUNT, commandCaptor.getValue().getAmount());

    }
}
