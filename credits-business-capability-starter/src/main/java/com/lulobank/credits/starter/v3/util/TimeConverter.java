package com.lulobank.credits.starter.v3.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TimeConverter {

    public static final LocalDateTime toUTC(final LocalDateTime localDateTime) {
        return ZonedDateTime.
                of(localDateTime, ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }
}
