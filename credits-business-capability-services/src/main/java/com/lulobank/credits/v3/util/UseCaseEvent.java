package com.lulobank.credits.v3.util;

@FunctionalInterface
public interface UseCaseEvent<T> {
    void execute(T command);
}
