package com.ndilsou.clickstream.processor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import com.ndilsou.clickstream.common.events.Event;

import io.atlassian.fugue.Try;

import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("default")
public class ProcessingLambda implements RequestHandler<KinesisEvent, Map<ProcessingStatus, Long>> {
  private final Logger logger = LoggerFactory.getLogger(ProcessingLambda.class);
  private static final ObjectReader OBJECT_READER = new ObjectMapper().readerFor(Event.class);

  @Inject
  ErrorSink errorSink;

  @Inject
  KinesisSink eventSink;

  @Inject
  EventTransformService transformService;

  @Override
  public Map<ProcessingStatus, Long> handleRequest(KinesisEvent event, Context ctx) {
    Map<ProcessingStatus, Long> statusReport =
        event.getRecords().stream().map(this::extractEventFromRecord).map(this::transformEvent)
            .map(this::sinkEvent).map(this::handleFailure)
            .collect(Collectors.groupingBy(status -> status, Collectors.counting()));
    return statusReport;
  }

  private Try<Event> extractEventFromRecord(KinesisEventRecord record) {
    Try<Event> tryEvent;
    try {
      Event event = OBJECT_READER.readValue(record.getKinesis().getData().array());
      tryEvent = Try.successful(event);
    } catch (Exception e) {
      tryEvent = Try.failure(new ProcessingException(e, record));
    }
    return tryEvent;
  }

  private Try<TransformedEvent> transformEvent(Try<Event> tryEvent) {
    return tryEvent.map(event -> transformService.transform(event));
  }

  private Try<ProcessingStatus> sinkEvent(Try<TransformedEvent> tryEvent) {
    return tryEvent.map(event -> {
      eventSink.sink(event);
      return ProcessingStatus.Success;
    });

  }

  private ProcessingStatus handleFailure(Try<ProcessingStatus> status) {
    return status.recover(ProcessingException.class, err -> {
      errorSink.sink(err);
      return ProcessingStatus.Failure;
    }).recover(err -> {
      logger.error("Critical failure, Events are being lost!!!", err);;
      return ProcessingStatus.CriticalFailure;
    }).getOrElse(() -> ProcessingStatus.CriticalFailure);
  }
}
