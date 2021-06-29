package com.lulobank.credits.starter.v3.adapters.out.loan;

import brave.SpanCustomizer;
import com.lulobank.credits.starter.v3.adapters.out.loan.mapper.SimulateLoanMapper;
import com.lulobank.credits.starter.v3.mappers.CreateLoanMapper;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.loan.dto.DisbursementLoanRequest;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.in.loan.dto.LoanResponse;
import com.lulobank.credits.v3.port.in.loan.dto.LoanV3Error;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePayment;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePaymentRequest;
import com.lulobank.tracing.FunctionBrave;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.CreateLoanRequest;
import flexibility.client.models.request.DisbursementRequest;
import flexibility.client.models.request.SimulatedLoanRequest;
import flexibility.client.models.response.CreateLoanResponse;
import flexibility.client.models.response.DisbursementResponse;
import flexibility.client.models.response.SimulatedLoanResponse;
import flexibility.client.sdk.FlexibilitySdk;
import flexibility.client.util.DisbursementRequestBuilder;
import io.vavr.CheckedFunction1;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lulobank.credits.v3.port.in.loan.LoanState.ACTIVE;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
@CustomLog
public class LoanAdapter implements LoanV3Service {

	private final FlexibilitySdk flexibilitySdk;
    private final CreditsConditionV3 creditsConditionV3;
    private final FunctionBrave functionBrave;
    private final SpanCustomizer spanCustomizer;

    @Override
    public Try<LoanResponse> create(LoanRequest loanCommand) {
        CreateLoanRequest createLoanRequest = CreateLoanMapper.INSTANCE.toCreateLoanRequest(loanCommand);
        return Try.of(() -> createLoanFn().apply(createLoanRequest))
                .map(CreateLoanMapper.INSTANCE::toLoanResponse)
                .onFailure(exception -> log.error("Error connecting with flexibility", exception));
    }
    
    @Override
	public Either<LoanV3Error, String> disbursementLoan(DisbursementLoanRequest disbursementLoanRequest) {
		return Try.of(() -> {
            DisbursementRequest disbursementRequest = buildDisbursementRequest(disbursementLoanRequest.getIdClientMambu(), disbursementLoanRequest.getIdCreditMambu());
            return disburseLoan().apply(disbursementRequest);
        })
				.map(disbursementResponse -> ACTIVE.name())
				.onFailure(ex -> log.error(
						String.format("Error disbursement loan id: %s", disbursementLoanRequest.getIdClient()), ex))
				.toEither()
				.mapLeft(this::mapError);
	}

    private CheckedFunction1<DisbursementRequest, DisbursementResponse> disburseLoan() {
        return functionBrave.decorateChecked("Flexy-Disbursement", flexibilitySdk::disburseLoan);
    }

    private LoanV3Error mapError(Throwable throwable) {
    	
    	return Match(throwable).of(
                Case($(instanceOf(ProviderException.class)), this::mapProviderException),
                Case($(), this::mapUnexpectedError));
	}

	private LoanV3Error mapUnexpectedError(Throwable e) {
		return new LoanV3Error("100", e.getMessage());
	}

	private LoanV3Error mapProviderException(ProviderException e) {
		return new LoanV3Error(e.getErrorCode(), e.getMessage());
	}

	private DisbursementRequest buildDisbursementRequest(String idClientMambu, String idCreditMambu) {
        return DisbursementRequestBuilder.disbursementRequest().withClientId(idClientMambu)
                .withLoanId(idCreditMambu).build();
    }

    private CheckedFunction1<CreateLoanRequest, CreateLoanResponse> createLoanFn() {
        return functionBrave.decorateChecked("Flexy-CreateLoan", flexibilitySdk::createLoanAccount);
    }

    @Override
    public Either<LoanV3Error, List<SimulatePayment>> simulateLoan(SimulatePaymentRequest simulatePaymentRequest) {
        spanCustomizer.tag("installments", String.valueOf(simulatePaymentRequest.getInstallment()));
        SimulatedLoanRequest simulatedLoanRequest = SimulateLoanMapper.INSTANCE.simulateLoanRequestFrom(simulatePaymentRequest, creditsConditionV3);
        return Try.of(() -> simulateLoanFn().apply(simulatedLoanRequest))
                .map(SimulatedLoanResponse::getRepayment)
                .map(mapRepayments())
                .map(toEither())
                .recover(ProviderException.class, exception -> {
                    log.error(String.format("Error in Sdk Flexibility ( flexibilitySdk.simulateLoan )  , msg : %s, code : %s", exception.getMessage(), exception.getErrorCode()), exception);
                    return Either.left(new LoanV3Error(exception.getErrorCode(), exception.getMessage()));
                }).get();
    }

    private CheckedFunction1<SimulatedLoanRequest, SimulatedLoanResponse> simulateLoanFn() {
        return functionBrave.decorateChecked("Flexy-SimulatedLoanRequest", flexibilitySdk::simulateLoan);
    }

    public Function<List<SimulatedLoanResponse.Repayment>, List<SimulatePayment>> mapRepayments() {
        return list -> list.stream()
                .map(SimulateLoanMapper.INSTANCE::simulatePaymentsFrom)
                .collect(Collectors.toList());
    }

    public Function<List<SimulatePayment>, Either<LoanV3Error, List<SimulatePayment>>> toEither() {
        return Either::right;
    }
}
