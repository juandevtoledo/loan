package com.lulobank.credits.v3.util;

import java.time.LocalDate;

public class LocalDateComparator {

    public static boolean isBeforeOrEquals(LocalDate date1, LocalDate date2) {
        return !date1.isAfter(date2);
    }

    public static boolean isAfterOrEquals(LocalDate date1, LocalDate date2) {
        return !date1.isBefore(date2);
    }

}
