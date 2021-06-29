package com.lulobank.credits.starter.v3.adapters.out.sqs;

import com.lulobank.events.api.Event;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@CustomLog
public abstract class SqsV3Integration<T, R> {

    private final String endpoint;

    protected SqsV3Integration(String endpoint) {
        this.endpoint = endpoint;
    }

    public abstract Event<R> map(T event);

    public void send(T event, Consumer<SqsCommand> consumer) {
        consumer.accept(new SqsCommand(map(event), endpoint));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class SqsCommand {

        private Event<R> event;
        private String endpoint;

    }

}
