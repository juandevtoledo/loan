package com.lulobank.credits.v3.usecase.payment.dto;

import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.service.CloseLoanService;
import com.lulobank.credits.v3.service.LoanPaymentService;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;

import java.math.BigDecimal;

import static com.lulobank.credits.v3.service.mapper.LoanPaymentMapper.*;
import static java.math.BigDecimal.ZERO;

@CustomLog
public enum PaymentType {

    MINIMUM_PAYMENT {
        @Override
        public Either<UseCaseResponseError, PaymentApplied> process(MakePaymentRequest payment, LoanPaymentService loanPaymentService,
                                                                   CoreBankingService coreBankingService, CloseLoanService closeLoanService) {
            log.info("Process Minimum payment , idClient : {}, loanId : {} , amount : {} ", payment.getIdCredit(), payment.getAmount(), payment.getLoanId());
            return loanPaymentService.makePayment(loanPaymentRequestFrom(payment))
                    .peek(paymentResponse -> log.info("Payment success to idClient : {} , amount : {} ", payment.getIdClient(), paymentResponse.getStatus()))
                    .peekLeft(error -> log.error("Error process payment ,  providerCode : {} , idClient : {} ", error.getProviderCode(), payment.getIdClient()));
        }
    },

    EXTRA_AMOUNT_PAYMENT {
        @Override
        public Either<UseCaseResponseError, PaymentApplied> process(MakePaymentRequest payment, LoanPaymentService loanPaymentService,
                                                                   CoreBankingService coreBankingService, CloseLoanService closeLoanService) {
            log.info("Process Extra payment , idClient : {}, loanId : {} , amount : {} ", payment.getIdCredit(), payment.getAmount(), payment.getLoanId());
            return loanPaymentService.makePayment(loanPaymentRequestFrom(payment))
                    .peek(paymentResponse -> log.info("Payment success to idClient : {} , amount : {} ", payment.getIdClient(), paymentResponse.getStatus()))
                    .peekLeft(error -> log.error("Error process payment , businessCode : {} , providerCode : {} , idClient : {} ", error.getBusinessCode(), error.getProviderCode(), payment.getIdClient()));
        }
    },

    TOTAL_PAYMENT {
        @Override
        public Either<UseCaseResponseError, PaymentApplied> process(MakePaymentRequest payment, LoanPaymentService loanPaymentService,
                                                                   CoreBankingService coreBankingService, CloseLoanService closeLoanService) {
            log.info("Process Total payment , idClient : {}, loanId : {} , amount : {} ", payment.getIdCredit(), payment.getAmount(), payment.getLoanId());
            return loanPaymentService.makePayment(loanPaymentRequestClosedFrom(payment))
                    .peek(paymentResponse -> log.info("Payment success to idClient : {} , amount : {} ", payment.getIdClient(), paymentResponse.getAmount()))
                    .peekLeft(error -> log.error("Error process payment , businessCode : {} , providerCode : {} , idClient : {} ", error.getBusinessCode(), error.getProviderCode(), payment.getIdClient()))
                    .flatMap(paymentApplied ->
                            closeLoanService.close(payment.getIdCredit(), payment.getLoanId())
                            .map(creditsV3Entity ->paymentApplied)
                    );

        }
    },

    MINIMUM_AND_EXTRA_AMOUNT_PAYMENTS {
        @Override
        public Either<UseCaseResponseError, PaymentApplied> process(MakePaymentRequest payment, LoanPaymentService loanPaymentService,
                                                                CoreBankingService coreBankingService, CloseLoanService closeLoanService) {

            log.info("Process MinimumAndExtraAmount payment , idClient : {}, loanId : {} , amount : {} ", payment.getIdCredit(), payment.getAmount(), payment.getLoanId());
            return coreBankingService.getLoanInformation(payment.getLoanId(), payment.getCoreBankingId())
                    .mapLeft(UseCaseResponseError::map)
                    .flatMap(loan ->
                            ZERO.compareTo(loan.getInstallmentExpectedDue().getValue()) == 0 ?
                                    extraAmountPayment(payment, loanPaymentService, payment.getAmount()) :
                                    minimumPayment(payment, loanPaymentService, loan)
                    );
        }

        private Either<UseCaseResponseError, PaymentApplied> minimumPayment(MakePaymentRequest payment, LoanPaymentService loanPaymentService, LoanInformation loan) {
            return loanPaymentService.makePayment(loanPaymentCustomAmountFrom(payment, loan.getInstallmentExpectedDue().getValue()))
                    .peek(firstPayment -> log.info("Success First Payment amount {} , idClient {} ", firstPayment.getAmount(), payment.getIdClient()))
                    .peekLeft(errorPayment -> log.info("Failed First Payment ProviderCode {} , idClient {} ", errorPayment.getProviderCode(), payment.getIdClient()))
                    .flatMap(firstPayment -> {
                                BigDecimal extraAmount =  payment.getAmount().subtract(loan.getInstallmentExpectedDue().getValue());
                                return extraAmountPayment(payment, loanPaymentService, extraAmount)
                                       .fold(error -> Either.right(firstPayment),success->successPayment(payment, success));
                            }
                    );
        }

        private Either<UseCaseResponseError, PaymentApplied> extraAmountPayment(MakePaymentRequest payment, LoanPaymentService loanPaymentService, BigDecimal amount) {
            return loanPaymentService.makePayment(loanPaymentCustomAmountFrom(payment, amount))
                    .peek(firstPayment -> log.info("Success Second Payment amount {} , idClient {} ", firstPayment.getAmount(), payment.getIdClient()))
                    .peekLeft(errorPayment -> log.info("Failed Second Payment ProviderCode {} , idClient {} ", errorPayment.getProviderCode(), payment.getIdClient()));

        }

        private Either<UseCaseResponseError, PaymentApplied> successPayment(MakePaymentRequest payment, PaymentApplied success) {
            return Either.right(
                    PaymentApplied.builder()
                            .amount(payment.getAmount())
                            .entryDate(success.getEntryDate())
                            .transactionId(success.getTransactionId())
                            .build()
            );
        }
    };

    public abstract Either<UseCaseResponseError, PaymentApplied> process(MakePaymentRequest payment, LoanPaymentService loanPaymentService,
                                                                        CoreBankingService coreBankingService, CloseLoanService closeLoanService);
}
