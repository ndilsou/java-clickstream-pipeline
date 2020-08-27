package com.ndilsou.clickstream.processor;

import com.ndilsou.clickstream.common.events.Event;

public interface Sink {
  boolean sinkEvent(Event event);
}
