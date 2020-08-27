package com.ndilsou.clickstream.processor;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.NotImplementedException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;

@Singleton
public class ErrorSink {
  private SqsClient sqs;

  private String queueUrl;


  /**
   * Delivers the error to the dead letter queue for offline handling.
   *
   * @param err the processing exception to handle.
   * @throws IOException if delivery or parsing fails.
   */
  public void sink(ProcessingException err) {
    DeadLetterMessage message = DeadLetterMessage.fromError(err);
    String messageBody;
    try {
      messageBody = message.toJson();

    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(
          String.format("ProcessingException %s cannot be parsed to json", err), e);
    }
    sqs.sendMessage(m -> m.queueUrl(queueUrl).messageBody(messageBody));
  }


  /**
   * Creates an ErrorSink.
   *
   * @param queueName the queue name
   * @throws IOException Thrown if the request to get the queue url fails.
   */
  public ErrorSink(@ConfigProperty(name = "gateway.producer.sqs.queue-name") String queueName)
      throws IOException {
    this.sqs = SqsClient.create();

    try {
      this.queueUrl = sqs.getQueueUrl(req -> req.queueName(queueName)).queueUrl();
    } catch (Exception e) {
      throw new IOException(String.format("GetQueueUrl failed for queue url for %s", queueName), e);
    }
  }

}
