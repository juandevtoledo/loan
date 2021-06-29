package com.lulobank.credits.v3.port.in.loan;

import com.lulobank.credits.v3.port.in.loan.dto.DisbursementLoanRequest;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.in.loan.dto.LoanResponse;
import com.lulobank.credits.v3.port.in.loan.dto.LoanV3Error;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePayment;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePaymentRequest;
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.util.List;

public interface LoanV3Service {

    Try<LoanResponse> create(LoanRequest loanCommand);
     
    Either<LoanV3Error, String> disbursementLoan(DisbursementLoanRequest disbursementLoanRequest);

    @Deprecated
    Either<LoanV3Error,List<SimulatePayment>> simulateLoan(SimulatePaymentRequest simulatePaymentRequest);

}
