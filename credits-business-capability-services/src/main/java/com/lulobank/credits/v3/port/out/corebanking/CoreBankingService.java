package com.lulobank.credits.v3.port.out.corebanking;

import com.lulobank.credits.v3.port.out.corebanking.dto.GetMovementsRequest;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanStatement;
import com.lulobank.credits.v3.port.out.corebanking.dto.ClientAccount;
import com.lulobank.credits.v3.port.out.corebanking.dto.CreatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.Movement;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePaymentRequest;
import io.vavr.control.Either;
import java.util.List;

public interface CoreBankingService {

    Either<CoreBankingError, Double> getInsuranceFee();

    Either<CoreBankingError, LoanInformation> getLoanInformation(String creditId, String clientId);

    Either<CoreBankingError, LoanStatement> getLoanStatement(String loanId, String clientIdCBS, String initialPeriod);

    Either<CoreBankingError, PaymentApplied> payment(CreatePayment createPayment);

    Either<CoreBankingError, List<ClientAccount>> getAccountsByClient(String clientCoreBankingId);

    Either<CoreBankingError,List<SimulatePayment>> simulateLoan(SimulatePaymentRequest simulatePaymentRequest);

    Either<CoreBankingError, List<Movement>> getLoanMovements(GetMovementsRequest request);
}
