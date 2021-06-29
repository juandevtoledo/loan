package com.lulobank.credits.v3.usecase.payment;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.service.CloseLoanService;
import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.service.dto.LoanPaymentRequest;
import com.lulobank.credits.v3.usecase.payment.dto.Payment;
import com.lulobank.credits.v3.usecase.payment.dto.PaymentResult;
import com.lulobank.credits.v3.usecase.payment.dto.PaymentType;
import com.lulobank.credits.v3.usecase.payment.dto.SubPaymentType;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.lulobank.credits.Constants.MAMBU_ACCOUNT_ID;
import static com.lulobank.credits.services.Constant.AMOUNT_PAYMENT;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static com.lulobank.credits.v3.util.EntitiesFactory.PaymentFactory.paymentCommand;
import static com.lulobank.credits.v3.util.LoanMockFactory.paid;
import static com.lulobank.credits.v3.util.LoanMockFactory.readyToPaid;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PaymentUseCaseTest {

    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private LoanPaymentService loanPaymentService;
    @Mock
    private CoreBankingService coreBankingService;
    @Mock
    private CloseLoanService closeLoanService;
    @Captor
    private ArgumentCaptor<LoanPaymentRequest> loanPaymentRequestCaptor;

    private PaymentUseCase paymentUseCase;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        paymentUseCase = new PaymentUseCase(loanPaymentService, coreBankingService, closeLoanService, creditsV3Repository);
    }

    @Test
    public void payment_WhenRequestIsMinimumPayment() {

        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.right(paymentApplied()));
        Payment payment = paymentCommand().paymentType(PaymentType.MINIMUM_PAYMENT).build();
        Either<UseCaseResponseError, PaymentResult> response = paymentUseCase.execute(payment);
        assertThat(response.isRight(), is(true));
        assertPaymentResult(response.get());
        LoanPaymentRequest loanPaymentRequest = loanPaymentRequestCaptor.getValue();
        assertLoanPaymentRequest(loanPaymentRequest, SubPaymentType.NONE.name(), false);
        Mockito.verify(closeLoanService,never()).close(anyString(),anyString());
        Mockito.verify(coreBankingService,never()).getLoanInformation(anyString(),anyString());
    }

    @Test
    public void payment_WhenRequestIsExtraAmountPayment() {

        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.right(paymentApplied()));
        Either<UseCaseResponseError, PaymentResult> response = paymentUseCase.execute(extraAmountPaymentRequest());
        assertThat(response.isRight(), is(true));
        assertPaymentResult(response.get());
        LoanPaymentRequest loanPaymentRequest = loanPaymentRequestCaptor.getValue();
        assertLoanPaymentRequest(loanPaymentRequest, SubPaymentType.AMOUNT_INSTALLMENT.name(), false);
        Mockito.verify(closeLoanService,never()).close(anyString(),anyString());
        Mockito.verify(coreBankingService,never()).getLoanInformation(anyString(),anyString());
    }

    @Test
    public void payment_WhenRequestIsTotalPayment() {
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.right(paymentApplied()));
        when(closeLoanService.close(anyString(),anyString())).thenReturn(Either.right(creditsEntityWithAcceptOffer()));
        Either<UseCaseResponseError, PaymentResult> response = paymentUseCase.execute(totalPaymentRequest());
        assertThat(response.isRight(), is(true));
        assertPaymentResult(response.get());
        LoanPaymentRequest loanPaymentRequest = loanPaymentRequestCaptor.getValue();
        assertLoanPaymentRequest(loanPaymentRequest, SubPaymentType.NONE.name(), true);
        Mockito.verify(coreBankingService,never()).getLoanInformation(anyString(),anyString());
    }

    @Test
    public void payment_WhenRequestIsMinimumAndExtraAmountPayment() {
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.right(paymentApplied()));
        when(closeLoanService.close(anyString(),anyString())).thenReturn(Either.right(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(anyString(),anyString())).thenReturn(Either.right(readyToPaid()));
        Either<UseCaseResponseError, PaymentResult> response = paymentUseCase.execute(minimumAndExtraAmountPaymentRequest());
        assertThat(response.isRight(), is(true));
        assertPaymentResult(response.get());
        Mockito.verify(loanPaymentService,times(2)).makePayment(any());
    }

    @Test
    public void payment_WhenRequestIsMinimumAndExtraAmountAndInstallmentDueIsZero() {
        when(creditsV3Repository.findById(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(loanPaymentService.makePayment(loanPaymentRequestCaptor.capture())).thenReturn(Either.right(paymentApplied()));
        when(closeLoanService.close(anyString(),anyString())).thenReturn(Either.right(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(anyString(),anyString())).thenReturn(Either.right(paid()));
        Either<UseCaseResponseError, PaymentResult> response = paymentUseCase.execute(minimumAndExtraAmountPaymentRequest());
        assertThat(response.isRight(), is(true));
        assertPaymentResult(response.get());
        Mockito.verify(loanPaymentService,times(1)).makePayment(any());
    }


    private void assertLoanPaymentRequest(LoanPaymentRequest loanPaymentRequest,String type, boolean paymentOff) {
        assertThat(loanPaymentRequest.getLoanId(),is("YAMW127"));
        assertThat(loanPaymentRequest.getAmount(),is(BigDecimal.valueOf(300000)));
        assertThat(loanPaymentRequest.getIdClient(),is("cfe4053d-9f55-40dd-98cc-6ee8a34cac43"));
        assertThat(loanPaymentRequest.getIdCredit(),is("014176ef-e291-4db6-9b49-18d253a8ae5d"));
        assertThat(loanPaymentRequest.getPaymentOff(),is(paymentOff));
        assertThat(loanPaymentRequest.getType(),is(type));
    }

    private void assertPaymentResult(PaymentResult paymentResult) {
        assertThat(paymentResult.getAmountPaid(), is(AMOUNT_PAYMENT));
        assertThat(paymentResult.getDate().toLocalDate(), is(LocalDate.now()));
        assertThat(paymentResult.getTransactionId(), is("transaction-id"));
    }

    private PaymentApplied paymentApplied() {
        return
                PaymentApplied.builder()
                        .amount(AMOUNT_PAYMENT)
                        .status("SUCCESS")
                        .entryDate(LocalDateTime.now())
                        .transactionId("transaction-id")
                        .build();
    }

    private Payment extraAmountPaymentRequest() {
        return paymentCommand().paymentType(PaymentType.EXTRA_AMOUNT_PAYMENT)
                .subPaymentType(SubPaymentType.AMOUNT_INSTALLMENT)
                .build();
    }

    private Payment totalPaymentRequest() {
        return paymentCommand().paymentType(PaymentType.TOTAL_PAYMENT)
                .subPaymentType(SubPaymentType.NONE)
                .build();
    }

    private Payment minimumAndExtraAmountPaymentRequest() {
        return paymentCommand().paymentType(PaymentType.MINIMUM_AND_EXTRA_AMOUNT_PAYMENTS)
                .subPaymentType(SubPaymentType.AMOUNT_INSTALLMENT)
                .build();
    }
}
