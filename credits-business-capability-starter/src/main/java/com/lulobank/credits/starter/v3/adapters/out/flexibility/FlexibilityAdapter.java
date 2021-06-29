package com.lulobank.credits.starter.v3.adapters.out.flexibility;

import com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper.AccountsMapper;
import com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper.LoanMapper;
import com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper.LoanStatementMapper;
import com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper.PaymentMapper;
import com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper.SimulateLoanMapper;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.ClientAccount;
import com.lulobank.credits.v3.port.out.corebanking.dto.CreatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.GetMovementsRequest;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanStatement;
import com.lulobank.credits.v3.port.out.corebanking.dto.Movement;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePaymentRequest;
import com.lulobank.credits.v3.port.out.corebanking.mapper.MovementMapper;
import com.lulobank.tracing.FunctionBrave;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetAccountRequest;
import flexibility.client.models.request.GetLoanMovementsRequest;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.models.request.GetLoanStatementRequest;
import flexibility.client.models.request.PaymentRequest;
import flexibility.client.models.request.SimulatedLoanRequest;
import flexibility.client.models.response.ConfigValueResponse;
import flexibility.client.models.response.GetAccountResponse;
import flexibility.client.models.response.GetLoanMovementsResponse;
import flexibility.client.models.response.GetLoanResponse;
import flexibility.client.models.response.GetLoanStatementResponse;
import flexibility.client.models.response.PaymentResponse;
import flexibility.client.models.response.SimulatedLoanResponse;
import flexibility.client.sdk.FlexibilitySdk;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.vavr.CheckedFunction1;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper.AccountsMapper.accountRequestTo;
import static com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper.PaymentMapper.paymentRequestTo;


@CustomLog
@RequiredArgsConstructor
public class FlexibilityAdapter implements CoreBankingService {

    private static final String INSURANCE_CONFIG_NAME = "loan.insurrance.rate";
    private static final String CIRCUIT_BREAKER_OPEN = "Circuit breaker OPEN flexibility, msg : {}";
    private final FlexibilitySdk flexibilitySdk;
    private final FunctionBrave functionBrave;
    private final CreditsConditionV3 creditsConditionV3;
    private final CircuitBreaker circuitBreaker;

    public Either<CoreBankingError, Double> getInsuranceFee() {
        return Try.of(() -> getConfigFn().apply(INSURANCE_CONFIG_NAME))
                .filter(config -> !config.isEmpty())
                .onFailure(CallNotPermittedException.class, error -> log.error(CIRCUIT_BREAKER_OPEN, error.getMessage(), error))
                .onFailure(error -> log.error("Error getting insurance fee to generate pre approved offer:{}", error.getMessage()))
                .toEither(CoreBankingError.getParametersError())
                .flatMap(this::mapFeeValue);
    }

    @Override
    public Either<CoreBankingError, LoanInformation> getLoanInformation(final String loanNumber, final String clientId) {
        GetLoanRequest getLoanRequest = new GetLoanRequest();
        getLoanRequest.setClientId(clientId);
        getLoanRequest.setLoanId(loanNumber);
        return Try.of(() -> loanByLoanAccountIdFn().apply(getLoanRequest))
                .map(LoanMapper.INSTANCE::coreBankingInformationTO)
                .onFailure(CallNotPermittedException.class, error -> log.error(CIRCUIT_BREAKER_OPEN, error.getMessage(), error))
                .onFailure(error -> log.error("Error getting loan information for  {} {}", clientId, error.getMessage()))
                .fold(e -> getCoreBankingError(e, CoreBankingError::buildGettingDataLoanError), Either::right);
    }
    
    @Override
    public Either<CoreBankingError, LoanStatement> getLoanStatement(final String loanId, final String clientIdCBS, final String statementDate) {
    	GetLoanStatementRequest loanStatement = new GetLoanStatementRequest();
        loanStatement.setClientId(clientIdCBS);
        loanStatement.setLoanNumber(loanId);
        loanStatement.setStatementDate(statementDate);
        return Try.of(() -> loanStatmentFn().apply(loanStatement))
                .map(loanStatementResp -> LoanStatementMapper.loanStatementResponse(loanStatementResp, statementDate))
                .onFailure(CallNotPermittedException.class, error -> log.error(CIRCUIT_BREAKER_OPEN, error.getMessage(), error))
                .onFailure(error -> log.error("Error getting loan statement for  {} {}", clientIdCBS, error.getMessage()))
                .fold(e -> getCoreBankingError(e, CoreBankingError::buildGettingDataLoanError), Either::right);
    }

    @Override
    public Either<CoreBankingError, PaymentApplied> payment(CreatePayment createPayment) {
        return
                Try.of(() -> paymentLoanFn().apply(paymentRequestTo(createPayment)))
                        .onFailure(CallNotPermittedException.class, error -> log.error(CIRCUIT_BREAKER_OPEN, error.getMessage(), error))
                        .onFailure(ProviderException.class, error -> log.error("Error processing payment, client : {} , msg : {} ,  code : {} , ", createPayment.getCoreBankingId(), error.getMessage(), error.getErrorCode(), error))
                        .fold(e -> getCoreBankingError(e, CoreBankingError::paymentError), success -> Either.right(PaymentMapper.paymentAppliedTo(success)));
    }

    @Override
    public Either<CoreBankingError, List<ClientAccount>> getAccountsByClient(String clientCoreBankingId) {
        return
                Try.of(() -> getAccountsByClientIdFn().apply(accountRequestTo(clientCoreBankingId)))
                        .onFailure(CallNotPermittedException.class, error -> log.error(CIRCUIT_BREAKER_OPEN, error.getMessage(), error))
                        .onFailure(ProviderException.class, error -> log.error("Error processing payment, client : {} , msg : {} ,  code : {} , ", clientCoreBankingId, error.getMessage(), error.getErrorCode(), error))
                        .fold(e -> getCoreBankingError(e, CoreBankingError::clientWithOutAccountsError), accounts -> Either.right(AccountsMapper.accountRequestTo(accounts)));
    }

    @Override
    public Either<CoreBankingError, List<SimulatePayment>> simulateLoan(SimulatePaymentRequest simulatePaymentRequest) {
        SimulatedLoanRequest simulatedLoanRequest = SimulateLoanMapper.INSTANCE.simulateLoanRequestFrom(simulatePaymentRequest, creditsConditionV3);
        return Try.of(() -> simulateLoanFn().apply(simulatedLoanRequest))
                .onFailure(CallNotPermittedException.class, error -> log.error(CIRCUIT_BREAKER_OPEN, error.getMessage(), error))
                .onFailure(ProviderException.class, error -> log.error("Error processing payment,  msg : {} ,  code : {} , ", error.getMessage(), error.getErrorCode(), error))
                .map(SimulatedLoanResponse::getRepayment)
                .map(SimulateLoanMapper.INSTANCE::simulatePaymentsFrom)
                .fold(e -> getCoreBankingError(e, CoreBankingError::simulateLoanError), Either::right);
    }

    @Override
    public Either<CoreBankingError, List<Movement>> getLoanMovements(GetMovementsRequest request) {
        return Try.of(() -> getLoanMovementsFn().apply(MovementMapper.INSTANCE.getLoanMovementsRequestFrom(request))
                .getLoanMovementList().stream()
                .map(MovementMapper.INSTANCE::getMovementFrom)
                .collect(Collectors.toList()))
                .onFailure(CallNotPermittedException.class, error -> log.error(CIRCUIT_BREAKER_OPEN, error.getMessage(), error))
                .onFailure(error -> log.error("Error getting movements, creditId {},  clientId {}, msg {}",
                        request.getLoanNumber(), request.getClientId(), error.getMessage()))
                .fold(e -> getCoreBankingError(e, CoreBankingError::buildGettingDataLoanError), Either::right);
    }

    private CheckedFunction1<String, List<ConfigValueResponse>> getConfigFn() {
        return Decorators.ofCheckedFunction(functionBrave.decorateChecked("Flexy-getConfig",
                    flexibilitySdk::getConfig))
                .withCircuitBreaker(circuitBreaker)
                .decorate();
    }

    private CheckedFunction1<GetLoanRequest, GetLoanResponse> loanByLoanAccountIdFn() {
        return Decorators.ofCheckedFunction(functionBrave.decorateChecked("Flexy-GetLoanByLoanAccountId",
                    flexibilitySdk::getLoanByLoanAccountId))
                .withCircuitBreaker(circuitBreaker)
                .decorate();
    }
    
    private CheckedFunction1<GetLoanStatementRequest, GetLoanStatementResponse> loanStatmentFn() {
        return Decorators.ofCheckedFunction(functionBrave.decorateChecked("Flexy-GetLoanStatement",
                    flexibilitySdk::getLoanStatement))
                .withCircuitBreaker(circuitBreaker)
                .decorate();
    }

    private CheckedFunction1<PaymentRequest, PaymentResponse> paymentLoanFn() {
        return Decorators.ofCheckedFunction(functionBrave.decorateChecked("Flexy-paymentLoan",
                    flexibilitySdk::paymentLoan))
                .withCircuitBreaker(circuitBreaker)
                .decorate();
    }

    private CheckedFunction1<GetAccountRequest, List<GetAccountResponse>> getAccountsByClientIdFn() {
        return Decorators.ofCheckedFunction(functionBrave.decorateChecked("Flexy-getAccountsByClientId",
                    flexibilitySdk::getAccountsByClientId))
                .withCircuitBreaker(circuitBreaker)
                .decorate();
    }

    private CheckedFunction1<SimulatedLoanRequest, SimulatedLoanResponse> simulateLoanFn() {
        return Decorators.ofCheckedFunction(functionBrave.decorateChecked("Flexy-SimulatedLoanRequest",
                    flexibilitySdk::simulateLoan))
                .withCircuitBreaker(circuitBreaker)
                .decorate();
    }

    private CheckedFunction1<GetLoanMovementsRequest, GetLoanMovementsResponse> getLoanMovementsFn() {
        return Decorators.ofCheckedFunction(functionBrave.decorateChecked("Flexy-GetLoanMovements",
                    flexibilitySdk::getLoanMovements))
                .withCircuitBreaker(circuitBreaker)
                .decorate();
    }


    private Either<CoreBankingError, Double> mapFeeValue(List<ConfigValueResponse> config) {
        return Option.ofOptional(config.stream().findFirst())
                .toTry()
                .mapTry(conf -> Double.valueOf(conf.getValue()))
                .toEither(CoreBankingError.getParametersError());
    }

    private <T> Either<CoreBankingError, T> getCoreBankingError(Throwable e, Function<String, CoreBankingError> toCoreBanking) {
        CoreBankingError coreBankingError = CoreBankingError.defaultError();
        if (e instanceof ProviderException) {
            ProviderException p = (ProviderException) e;
            coreBankingError = toCoreBanking.apply(p.getErrorCode());
        }
        return Either.left(coreBankingError);
    }

}
