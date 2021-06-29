package com.lulobank.credits.v3.service;

@FunctionalInterface
public interface AsyncService<R, T> {

    R execute(T t);

}
