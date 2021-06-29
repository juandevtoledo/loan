package com.lulobank.credits.v3.usecase.payment;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.service.CloseLoanService;
import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.usecase.payment.dto.MakePaymentRequest;
import com.lulobank.credits.v3.usecase.payment.dto.Payment;
import com.lulobank.credits.v3.usecase.payment.dto.PaymentResult;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import static com.lulobank.credits.v3.usecase.payment.dto.SubPaymentType.NONE;

@CustomLog
@RequiredArgsConstructor
public class PaymentUseCase implements UseCase<Payment, Either<UseCaseResponseError, PaymentResult>> {

    private final LoanPaymentService loanPaymentService;
    private final CoreBankingService coreBankingService;
    private final CloseLoanService closeLoanService;
    private final CreditsV3Repository creditsV3Repository;

    @Override
    public Either<UseCaseResponseError, PaymentResult> execute(Payment command) {
        log.info("Processing loan payment, idClient : {}, loanId : {} , amount : {} ", command.getClientId(), command.getAmount());
        return creditsV3Repository.findById(command.getCreditId())
                .toEither(CoreBankingError.clientWithOutAccountsError())
                .map(creditsV3Entity -> makePaymentRequest(command, creditsV3Entity))
                .mapLeft(UseCaseResponseError::map)
                .flatMap(makePaymentRequest -> command.getPaymentType().process(makePaymentRequest, loanPaymentService, coreBankingService, closeLoanService))
                .map(this::toPaymentResult);
    }

    private MakePaymentRequest makePaymentRequest(Payment command, CreditsV3Entity creditsV3Entity) {
        return MakePaymentRequest.builder()
                .amount(command.getAmount())
                .idClient(creditsV3Entity.getIdClient())
                .idCredit(creditsV3Entity.getIdCredit().toString())
                .loanId(creditsV3Entity.getIdLoanAccountMambu())
                .coreBankingId(creditsV3Entity.getIdClientMambu())
                .type(Option.of(command.getSubPaymentType()).getOrElse(NONE))
                .build();
    }

    private PaymentResult toPaymentResult(PaymentApplied paymentApplied) {
        return PaymentResult.builder()
                .date(paymentApplied.getEntryDate())
                .amountPaid(paymentApplied.getAmount())
                .transactionId(paymentApplied.getTransactionId())
                .build();
    }
}
