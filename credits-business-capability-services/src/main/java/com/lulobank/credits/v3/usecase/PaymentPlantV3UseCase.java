package com.lulobank.credits.v3.usecase;

import com.lulobank.credits.v3.dto.ErrorUseCaseV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.PaymentPlanUseCaseResponseV3;
import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.mapper.PaymentPlanUseCaseResponseV3Mapper;
import com.lulobank.credits.v3.mapper.PaymentPlanV3Mapper;
import com.lulobank.credits.v3.mapper.SimulatePaymentRequestMapper;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.loan.dto.LoanV3Error;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePayment;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePaymentRequest;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.usecase.paymentplan.command.GetPaymentPlan;
import com.lulobank.credits.v3.util.UseCase;
import io.vavr.control.Either;
import lombok.CustomLog;
import org.owasp.encoder.Encode;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Deprecated
@CustomLog
public class PaymentPlantV3UseCase implements UseCase<GetPaymentPlan, Either<ErrorUseCaseV3, PaymentPlanUseCaseResponseV3>> {

    private final CreditsV3Repository creditsV3Repository;
    private final LoanV3Service loanV3Service;

    public PaymentPlantV3UseCase(CreditsV3Repository creditsV3Repository, LoanV3Service loanV3Service) {
        this.creditsV3Repository = creditsV3Repository;
        this.loanV3Service = loanV3Service;
    }

    @Override
    public Either<ErrorUseCaseV3, PaymentPlanUseCaseResponseV3> execute(GetPaymentPlan command) {

        return creditsV3Repository.findOfferEntityV3ByIdClient(command.getIdClient(), command.getIdCredit(), command.getIdOffer())
                .map(getPaymentList(command))
                .getOrElse(() -> Either.left(new ErrorUseCaseV3("Offer Not Found", 404, "404")));

    }

    private Function<OfferEntityV3, Either<ErrorUseCaseV3, PaymentPlanUseCaseResponseV3>> getPaymentList(GetPaymentPlan command) {
        return offerEntity -> {
            SimulatePaymentRequest simulatePaymentRequest = SimulatePaymentRequestMapper.INSTANCE
                    .simulatePaymentRequestFrom(offerEntity, command);
            return loanV3Service.simulateLoan(simulatePaymentRequest)
                    .map(getPaymentPlan())
                    .fold(getError(command), getEitherResponse(offerEntity))
                    .peek(calculatePendingBalance());
        };
    }

    private Function<List<PaymentV3>, Either<ErrorUseCaseV3, PaymentPlanUseCaseResponseV3>> getEitherResponse(OfferEntityV3 offerEntityV3) {
        return paymentPlanV3s ->
                Either.right(PaymentPlanUseCaseResponseV3Mapper.INSTANCE.paymentPlanUseCaseResponseV3MapperFrom(offerEntityV3, paymentPlanV3s));
    }


    private Function<LoanV3Error, Either<ErrorUseCaseV3, PaymentPlanUseCaseResponseV3>> getError(GetPaymentPlan command) {
        return error -> {
            log.error(String.format("Error in core banking , idClient : %s, msg: %s ", Encode.forJava(command.getIdClient()), error.getError()), error);
            return Either.left(new ErrorUseCaseV3("Error in core banking ", 502, error.getCode(), error.getError()));
        };
    }


    private Function<List<SimulatePayment>, List<PaymentV3>> getPaymentPlan() {
        AtomicInteger counter = new AtomicInteger(1);
        return simulatePayments ->
                simulatePayments
                        .stream()
                        .map(simulatePayment -> PaymentPlanV3Mapper.INSTANCE.paymentPlanV3From(simulatePayment, counter.getAndIncrement()))
                        .collect(Collectors.toList());
    }

    private Consumer<PaymentPlanUseCaseResponseV3> calculatePendingBalance() {
        return paymentPlan ->
                paymentPlan.getPaymentPlan()
                        .stream()
                        .map(current -> {
                            if (current.getInstallment() == 1)
                                current.setPendingBalance(paymentPlan.getPrincipalDebit().subtract(current.getPrincipalDue()));
                            return current;
                        })
                        .reduce((previous, current) -> {
                            current.setPendingBalance(previous.getPendingBalance().subtract(current.getPrincipalDue()));
                            return current;
                        });
    }
}
