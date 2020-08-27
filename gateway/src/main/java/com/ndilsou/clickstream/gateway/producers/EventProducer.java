package com.ndilsou.clickstream.gateway.producers;

import com.ndilsou.clickstream.common.events.Event;

import io.quarkus.arc.DefaultBean;

@DefaultBean
public interface EventProducer {
  AppendEventResult append(Event record);
}
