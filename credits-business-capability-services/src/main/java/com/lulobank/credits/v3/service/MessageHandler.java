package com.lulobank.credits.v3.service;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.lulobank.credits.v3.events.EventV3;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MessageHandler<T> {

  /**
   * This field make possible the right deserialization of raw message events
   */
  private final Type eventType;

  public void process(String message) {
    EventV3<T> messageEvent = new Gson().fromJson(message, this.eventType);
    apply(messageEvent.getPayload());
  }

  protected abstract void apply(T payload);

}
