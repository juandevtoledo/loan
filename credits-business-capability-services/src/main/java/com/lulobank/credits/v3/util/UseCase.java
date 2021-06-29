package com.lulobank.credits.v3.util;

@FunctionalInterface
public interface UseCase<T, R> {
    R execute(T command);
}