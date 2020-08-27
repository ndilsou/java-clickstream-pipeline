package com.ndilsou.clickstream.gateway.producers;

import com.ndilsou.clickstream.common.events.Event;

import io.quarkus.arc.AlternativePriority;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import javax.enterprise.inject.Typed;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
@Named("in-memory")
@Typed(EventProducer.class)
@AlternativePriority(1)
public class InMemoryEventProducer implements EventProducer {
  private final Logger logger = LoggerFactory.getLogger(EventProducer.class);

  private ArrayBlockingQueue<Event> records = new ArrayBlockingQueue<>(10);

  @Override
  public AppendEventResult append(Event event) {
    logger.info(event.toString());
    AppendEventResult result;
    try {
      if (records.size() >= 5) {
        throw new ArrayIndexOutOfBoundsException("Log is full");
      }
      records.add(event);
      result = AppendEventResult.accepted();

    } catch (Exception e) {
      String message = e.getMessage();
      logger.error(message, e);
      result = AppendEventResult.rejected(message);
    }
    return result;
  }

}
