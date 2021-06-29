package com.lulobank.credits.services.outboundadapters.dynamoconverter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {
    @Override
    public String convert(LocalDateTime object) {
        return object.toString();
    }

    @Override
    public LocalDateTime unconvert(String object) {
        return LocalDateTime.parse(object);
    }
}
