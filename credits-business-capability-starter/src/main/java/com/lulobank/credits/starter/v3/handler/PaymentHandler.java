package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.payment.dto.PaymentRequest;
import com.lulobank.credits.starter.v3.adapters.in.payment.dto.PaymentResponse;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.v3.usecase.payment.PaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.dto.Payment;
import com.lulobank.credits.v3.usecase.payment.dto.PaymentResult;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;
import static com.lulobank.credits.starter.v3.util.TimeConverter.toUTC;

@CustomLog
@RequiredArgsConstructor
public class PaymentHandler {

    private final PaymentUseCase paymentUseCase;

    public ResponseEntity<AdapterResponse> makePayment(PaymentRequest paymentRequest, String idClient) {
        return paymentUseCase.execute(getPaymentCustomInstallment(paymentRequest, idClient))
                .fold(this::mapError, this::mapResponse);
    }

    private ResponseEntity<AdapterResponse> mapResponse(PaymentResult result) {
        return new ResponseEntity<>(getPaymentResponse(result), HttpStatus.OK);
    }

    private PaymentResponse getPaymentResponse(PaymentResult result) {
        return PaymentResponse.builder()
               .date(toUTC(result.getDate()))
               .amountPaid(result.getAmountPaid())
               .transactionId(result.getTransactionId())
               .build();
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }

    private Payment getPaymentCustomInstallment(PaymentRequest paymentRequest, String idClient) {
        return Payment.builder()
                .creditId(paymentRequest.getIdCredit())
                .clientId(idClient)
                .amount(paymentRequest.getAmount())
                .paymentType(paymentRequest.getPaymentType())
                .subPaymentType(paymentRequest.getSubPaymentType())
                .build();
    }
}
