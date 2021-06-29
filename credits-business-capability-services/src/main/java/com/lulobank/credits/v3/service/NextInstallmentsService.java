package com.lulobank.credits.v3.service;

import com.lulobank.credits.sdk.dto.clientloandetail.Installment;
import com.lulobank.credits.services.domain.CreditsConditionDomain;
import com.lulobank.credits.v3.service.dto.InstallmentDetail;
import com.lulobank.credits.v3.service.dto.LoanDetail;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityInstallmentPendingStateEnum.PARTIALLY_PAID;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityInstallmentPendingStateEnum.PENDING;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityLoanMovementsPaidStatesEnum.PAID;
import static com.lulobank.credits.services.utils.DatesUtil.TIMESTAMP_FORMAT;
import static com.lulobank.credits.services.utils.DatesUtil.getLocalDateTimeByFormatter;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@RequiredArgsConstructor
public class NextInstallmentsService {

    private final CreditsConditionDomain creditsConditionDomain;

    public Installment get(LoanDetail loanDetail) {
        Optional<InstallmentDetail> installmentPaid = getCurrentInstallmentPaid(loanDetail.getInstallments());
        return Installment.builder()
                .amount(getAmountInstallment(loanDetail, installmentPaid))
                .balance(loanDetail.getBalance())
                .dueOn(getLocalDateTimeByFormatter(getDueDate(loanDetail), TIMESTAMP_FORMAT))
                .disableMinimumPayment(isMinimumPaymentPassed(getDueDate(loanDetail)))
                .installmentPaid(installmentPaid.isPresent())
                .lastPaidDate(getLastPaidDay(installmentPaid))
                .build();
    }

    private BigDecimal getAmountInstallment(LoanDetail loanDetail, Optional installmentPaid) {
        return installmentPaid.isPresent() ? BigDecimal.valueOf(creditsConditionDomain.getFeeAmountInstallement()) : loanDetail.getAmountInstallment();
    }


    private String getLastPaidDay(Optional<InstallmentDetail> installmentPaid) {
        return installmentPaid
                .map(InstallmentDetail::getDueDate)
                .map(date -> getLocalDateTimeByFormatter(date, TIMESTAMP_FORMAT))
                .orElse(EMPTY);
    }

    private LocalDateTime getDueDate(LoanDetail loanDetail) {
        return Option.ofOptional(loanDetail.getInstallments().stream()
                .filter(p -> PENDING.name().equals(p.getState()) || PARTIALLY_PAID.name().equals(p.getState()))
                .map(InstallmentDetail::getDueDate)
                .findFirst())
                .getOrElse(LocalDateTime.now());
    }

    private boolean isMinimumPaymentPassed(LocalDateTime dueDate) {
        long dayOfDueDateToNow = Duration.between(LocalDateTime.now(), dueDate).toDays();
        return dayOfDueDateToNow < 0 || dayOfDueDateBetweenMinimumPay(dayOfDueDateToNow);
    }

    private boolean dayOfDueDateBetweenMinimumPay(long dayOfDueDateToNow) {
        return dayOfDueDateToNow > creditsConditionDomain.getMinimumPayDay() && dayOfDueDateToNow < creditsConditionDomain.getMinimumPayDay();
    }


    private Optional<InstallmentDetail> getCurrentInstallmentPaid(List<InstallmentDetail> installmentDetails) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return installmentDetails.stream()
                .filter(installment -> PAID.name().equals(installment.getState()))
                .filter(p -> paidInSameMonth(localDateTime, p) || paidAfterCurrentTime(localDateTime, p))
                .findFirst();
    }

    private boolean paidAfterCurrentTime(LocalDateTime localDateTime, InstallmentDetail p) {
        return p.getDueDate().isAfter(localDateTime);
    }

    private boolean paidInSameMonth(LocalDateTime localDateTime, InstallmentDetail p) {
        return isSameMonth(localDateTime, p) || isSameMonth(localDateTime.plusMonths(1), p);
    }

    private boolean isSameMonth(LocalDateTime localDateTime, InstallmentDetail installment) {
        return YearMonth.from(localDateTime).equals(YearMonth.from(installment.getDueDate()));
    }

}
