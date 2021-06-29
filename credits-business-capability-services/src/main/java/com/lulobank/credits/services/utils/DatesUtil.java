package com.lulobank.credits.services.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DatesUtil {
    private DatesUtil(){
    }

    public static final DateTimeFormatter TIMESTAMP_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            .toFormatter();

    public static final String getLocalDateTimeByFormatter(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter){
            return dateTimeFormatter.format(localDateTime);
    }
}
