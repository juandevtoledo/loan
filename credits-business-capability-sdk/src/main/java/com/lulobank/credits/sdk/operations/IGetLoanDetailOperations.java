package com.lulobank.credits.sdk.operations;

import com.lulobank.credits.sdk.dto.clientloandetail.Loan;
import com.lulobank.credits.sdk.dto.loandetails.LoanDetailRetrofitResponse;

import java.util.List;
import java.util.Map;

public interface IGetLoanDetailOperations {

    LoanDetailRetrofitResponse getCreditsProducts(Map<String, String> headers, String idClient);

    List<Loan> getClientLoan(Map<String, String> headers, String idClient);
}
