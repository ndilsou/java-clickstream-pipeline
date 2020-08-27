package com.ndilsou.clickstream.processor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import lombok.Getter;

import org.apache.commons.lang3.exception.ExceptionUtils;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class DeadLetterMessage {
  private static final ObjectWriter OBJECT_WRITER =
      new ObjectMapper().writerFor(DeadLetterMessage.class);

  @Getter
  private final Object source;
  @Getter
  private final String errorMessage;
  @Getter
  private final String stackTrace;
  @Getter
  private final OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);

  /**
   * Creates a DeadLetterMessage.
   *
   * @param errorMessage the errorMessage to capture.
   * @param sourceEvent  the event that failed.
   * @param causeName    the name of the exception that caused the failure, can be null.
   */
  public DeadLetterMessage(final String errorMessage, String stackTrace, final Object source) {
    this.errorMessage = errorMessage;
    this.stackTrace = stackTrace;
    this.source = source;

  }


  public String toJson() throws JsonProcessingException {
    return OBJECT_WRITER.writeValueAsString(this);
  }

  /**
   * Creates a deadletter message from a processing error.
   *
   * @param err the processing error to read.
   * @return a new DeadLetterMessage
   */
  public static DeadLetterMessage fromError(final ProcessingException err) {
    String stacktrace = ExceptionUtils.getStackTrace(err);

    return new DeadLetterMessage(err.getMessage(), stacktrace, err.getSource());

  }

}
