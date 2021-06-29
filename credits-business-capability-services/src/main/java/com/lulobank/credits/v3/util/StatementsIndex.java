package com.lulobank.credits.v3.util;

import io.vavr.control.Option;

import java.time.LocalDate;

import static com.lulobank.credits.v3.port.in.loan.LoanState.APPROVED;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class StatementsIndex {

    private static final int CALENDAR_DAY = 30;

    public static String get(Integer dayOfPay) {
        return Option.of(dayOfPay)
                .map(StatementsIndex::minus10Days)
                .map(dayMinus10 -> String.valueOf(dayMinus10).concat("#").concat(APPROVED.name()))
                .getOrElse(EMPTY);
    }

    public static String getFilterExpression() {
        int lastDay = LocalDate.now().minusDays(1).getDayOfMonth();
        return String.valueOf(lastDay).concat("#").concat(APPROVED.name());
    }

    private static Integer minus10Days(int dayOfPay) {
        int dayReport = dayOfPay - 10;
        return (dayReport < 0) ? (CALENDAR_DAY + dayReport) : dayReport;
    }
}
