package com.lulobank.credits.v3.usecase.paymentplan;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import com.lulobank.credits.v3.port.out.corebanking.mapper.PaymentPlanMapper;
import com.lulobank.credits.v3.usecase.paymentplan.command.GetPaymentPlan;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lulobank.credits.v3.port.out.corebanking.mapper.SimulateLoanMapper.simulatePaymentRequestFrom;
import static com.lulobank.credits.v3.usecase.paymentplan.mapper.PaymentPlanUseCaseResponseMapper.paymentPlanUseCaseResponseMapperFrom;

@CustomLog
@AllArgsConstructor
public class PaymentPlantUseCase implements UseCase<GetPaymentPlan, Either<UseCaseResponseError, PaymentPlanUseCaseResponse>> {

    private final CreditsV3Repository creditsV3Repository;
    private final CoreBankingService coreBankingService;

    @Override
    public Either<UseCaseResponseError, PaymentPlanUseCaseResponse> execute(GetPaymentPlan command) {
        return creditsV3Repository.findOfferEntityV3ByIdClient(command.getIdClient(), command.getIdCredit(), command.getIdOffer())
                .toEither(CreditsError::databaseError)
                .flatMap(offerEntity -> simulate(offerEntity, command))
                .mapLeft(Function.identity());
    }

    private Either<CreditsError, PaymentPlanUseCaseResponse> simulate(OfferEntityV3 offerEntity, GetPaymentPlan command) {
        return coreBankingService.simulateLoan( simulatePaymentRequestFrom(offerEntity, command))
                .map(simulatePayments -> toPaymentPlan(simulatePayments, new AtomicReference<BigDecimal>(BigDecimal.valueOf(offerEntity.getAmount())), new AtomicInteger(1)))
                .map(paymentPlanV3s -> paymentPlanUseCaseResponseMapperFrom(offerEntity, paymentPlanV3s, command))
                .mapLeft(CreditsError::toCreditError);
    }

    private List<PaymentV3> toPaymentPlan(List<SimulatePayment> simulatePayments, AtomicReference<BigDecimal> pendingBalAccumulate, AtomicInteger counter) {
        return simulatePayments.stream()
                .map(simulatePayment -> {
                    BigDecimal pendingBalance = pendingBalAccumulate.accumulateAndGet(simulatePayment.getPrincipalDue(), BigDecimal::subtract);
                    return PaymentPlanMapper.INSTANCE.paymentPlanV3From(simulatePayment, pendingBalance, counter.getAndIncrement());
                }).collect(Collectors.toList());
    }

}
