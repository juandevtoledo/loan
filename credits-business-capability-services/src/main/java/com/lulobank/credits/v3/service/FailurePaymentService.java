package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.ClientAccount;
import com.lulobank.credits.v3.port.out.corebanking.dto.CreatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.TypePayment;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.usecase.automaticdebit.command.ProcessPayment;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static com.lulobank.credits.services.utils.SavingAccountState.ACTIVE;
import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper.SchedulerAutomaticDebitMapper.transactionRequest;

@CustomLog
@RequiredArgsConstructor
public class FailurePaymentService {

    private final CoreBankingService coreBankingService;
    private final SchedulerTransactionAsyncService schedulerAsyncService;

    public Try<ProcessPayment> retry(ProcessPayment processPayment) {
        return coreBankingService.getAccountsByClient(processPayment.getIdCoreBanking())
                .flatMap(accounts -> payment(accounts, processPayment))
                .toTry()
                .onFailure(error -> log.error("Error retrying payment , msg {} , idClient {}  ", error.getMessage(), processPayment.getIdClient()))
                .onSuccess(success -> log.info("Success retrying payment , idClient {} ", processPayment.getIdClient()))
                .andFinally(() -> schedulerAsyncService.retryTransaction(transactionRequest(processPayment)));
    }

    private Either<CoreBankingError, ProcessPayment> payment(List<ClientAccount> accounts, ProcessPayment processPayment) {
        return activeAccount(accounts)
                .toEither(CoreBankingError.clientWithOutAccountsError())
                .flatMap(account -> coreBankingService.payment(createPayment(processPayment, account)))
                .map(paymentApplied -> processPayment);
    }

    private Option<ClientAccount> activeAccount(java.util.List<ClientAccount> accounts) {
        return Option.ofOptional(accounts.stream()
                .filter(account -> ACTIVE.name().equals(account.getStatus()))
                .findFirst());
    }

    private CreatePayment createPayment(ProcessPayment processPayment, ClientAccount account) {
        return CreatePayment.builder()
                .amount(determineAmount(account))
                .loanId(processPayment.getIdLoanAccountMambu())
                .accountId(account.getNumber())
                .coreBankingId(processPayment.getIdCoreBanking())
                .payOff(false)
                .type(TypePayment.NONE)
                .build();
    }

    private BigDecimal determineAmount(ClientAccount account) {
        return account.isGmf() ? account.getBalance() : getAmountWithGMFIncluded(account);
    }

    private BigDecimal getAmountWithGMFIncluded(ClientAccount account) {
        return account.getBalance().divide(BigDecimal.valueOf(1.004), 2, BigDecimal.ROUND_DOWN);
    }

}
