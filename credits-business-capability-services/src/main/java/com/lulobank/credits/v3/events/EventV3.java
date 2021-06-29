package com.lulobank.credits.v3.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventV3<T> {

    private String id;
    private String eventType;
    private T payload;

}
