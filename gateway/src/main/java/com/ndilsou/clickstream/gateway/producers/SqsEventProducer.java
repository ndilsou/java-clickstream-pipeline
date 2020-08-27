package com.ndilsou.clickstream.gateway.producers;

import com.ndilsou.clickstream.common.events.Event;
import com.ndilsou.clickstream.gateway.serializers.EventSerializer;

import io.quarkus.arc.DefaultBean;

import java.io.IOException;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Singleton
@Named("sqs")
@Typed(EventProducer.class)
@DefaultBean
public class SqsEventProducer implements EventProducer {
  private final String queueUrl;
  private final Logger logger = LoggerFactory.getLogger(EventProducer.class);
  private final SqsClient sqsClient;

  @Inject
  private EventSerializer serializer;

  public SqsEventProducer(
      @ConfigProperty(name = "gateway.producer.sqs.queue-name") String queueName,
      EventSerializer serializer) throws IOException {
    this.sqsClient = SqsClient.create();

    GetQueueUrlRequest request = GetQueueUrlRequest.builder().queueName(queueName).build();

    try {
      this.queueUrl = sqsClient.getQueueUrl(request).queueUrl();
    } catch (Exception e) {
      throw new IOException(String.format("GetQueueUrl failed for queue url for %s", queueName), e);
    }

    this.serializer = serializer;
  }

  @Override
  public AppendEventResult append(Event record) {
    return sendMessage(record);
  }

  private AppendEventResult sendMessage(Event record) {
    return serializer.serialize(record).map((messageBody) -> {
      sqsClient.sendMessage(m -> m.queueUrl(queueUrl).messageBody(messageBody));
      return true;
    }).fold((e) -> {
      String errorMessage = e.getMessage();
      logger.error(errorMessage);
      return AppendEventResult.rejected(errorMessage);
    }, (ok) -> AppendEventResult.accepted());
  }

}
