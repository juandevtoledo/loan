package com.lulobank.credits.v3.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class LogMapper {

    @SneakyThrows
    public static <T> String  getJson(T object){
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
