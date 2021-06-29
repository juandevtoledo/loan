package com.lulobank.credits.starter.v3.util;

import com.lulobank.credits.services.utils.SavingAccountState;
import com.lulobank.credits.v3.port.out.corebanking.dto.GetMovementsRequest;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePaymentRequest;
import com.lulobank.credits.v3.usecase.movement.dto.MovementType;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.response.GetAccountResponse;
import flexibility.client.models.response.GetLoanMovementsResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT_MAMBU;
import static com.lulobank.credits.starter.utils.Constants.ID_LOAN_ACCOUNT_MAMBU;
import static com.lulobank.credits.starter.utils.Constants.LIMIT;
import static com.lulobank.credits.starter.utils.Constants.OFFSET;

public class FlexibilityFactory {

    public static class LoanInformationFactory {

        public static final Double BASE_AMOUNT = 1000000d;

        public static GetMovementsRequest buildGetMovementsRequest() {
            return GetMovementsRequest.builder().clientId(ID_CLIENT_MAMBU).loanNumber(ID_LOAN_ACCOUNT_MAMBU)
                    .offset(OFFSET).limit(LIMIT).build();
        }

        public static GetLoanMovementsResponse buildLoanMovementsResponseError() throws ProviderException {
            throw  new ProviderException("Error", "502");
        }

        public static GetLoanMovementsResponse buildLoanMovementsResponse() {
            GetLoanMovementsResponse loanMovementsResponse = new GetLoanMovementsResponse();
            loanMovementsResponse.setLoanMovementList(buildLoanMovements());
            return loanMovementsResponse;
        }

        public static GetLoanMovementsResponse buildLoanMovementsResponseDelayed() throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
            return buildLoanMovementsResponse();
        }

        private static List<GetLoanMovementsResponse.LoanMovement> buildLoanMovements() {
            return IntStream.range(0, 5)
                    .mapToObj(LoanInformationFactory::buildLoanMovement)
                    .collect(Collectors.toList());
        }

        private static GetLoanMovementsResponse.LoanMovement buildLoanMovement(int factor) {
            GetLoanMovementsResponse.LoanMovement movement = new GetLoanMovementsResponse.LoanMovement();
            movement.setAmount(BASE_AMOUNT + (factor * 100));
            movement.setAmounts(buildAmounts(movement.getAmount()));
            movement.setTransactionType(MovementType.LOAN_REPAYMENT.name());
            movement.setCreationDate(LocalDateTime.now());
            return movement;
        }

        private static GetLoanMovementsResponse.Amounts buildAmounts(Double amount) {
            GetLoanMovementsResponse.Amounts amounts = new GetLoanMovementsResponse.Amounts();
            amounts.setFeesAmount(amount * 0.1d);
            amounts.setPrincipalAmount(amount * 0.6d);
            amounts.setInterestAmount(amount * 0.3d);
            return amounts;
        }

        public static SimulatePaymentRequest buildSimulatePaymentRequest() {
            return SimulatePaymentRequest.builder()
                    .amount(BigDecimal.valueOf(200000))
                    .dayOfPay(15)
                    .interestRate(BigDecimal.valueOf(1.6f))
                    .installment(12)
                    .build();
        }
    }
    public static class AccountsInformationFactory {

        public static List<GetAccountResponse> buildListAccountResponse() {
            List<GetAccountResponse> accounts =  new ArrayList<>();
            GetAccountResponse accountResponse = new GetAccountResponse();
            accountResponse.setNumber("1999982388");
            accountResponse.setState(SavingAccountState.ACTIVE.name());
            GetAccountResponse.Balance balance = new GetAccountResponse.Balance();
            balance.setAmount(10000.0);
            accountResponse.setBalance(balance);
            accounts.add(accountResponse);
            return accounts;
        }

        public static List<GetAccountResponse> buildListAccountResponseError() throws ProviderException {
            throw  new ProviderException("Error", "502");
        }
    }
}
