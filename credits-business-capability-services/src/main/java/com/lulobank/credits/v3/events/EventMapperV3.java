package com.lulobank.credits.v3.events;

import java.util.UUID;

public class EventMapperV3 {

    private EventMapperV3(){}

    public static <T> EventV3<T> of(T t) {
        EventV3<T> event = new EventV3();
        event.setEventType(t.getClass().getSimpleName());
        event.setPayload(t);
        event.setId(UUID.randomUUID().toString());
        return event;
    }
}
