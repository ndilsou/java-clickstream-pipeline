package com.ndilsou.clickstream.gateway.producers;

import com.ndilsou.clickstream.common.events.Event;
import com.ndilsou.clickstream.gateway.serializers.EventSerializer;

import io.atlassian.fugue.Checked;
import io.atlassian.fugue.Try;

import java.nio.charset.Charset;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;

/*

*/
@Singleton
@Named("kinesis")
@Typed(EventProducer.class)
public class KinesisEventProducer implements EventProducer {
  KinesisClient kinesisClient;
  private final Logger logger = LoggerFactory.getLogger(EventProducer.class);
  private final String streamName;
  @Inject
  private final EventSerializer serializer;

  KinesisEventProducer(
      @ConfigProperty(name = "gateway.producer.kinesis.stream-name") String streamName,
      EventSerializer serializer) {
    this.streamName = streamName;
    this.serializer = serializer;
    this.kinesisClient = KinesisClient.create();
  }

  @Override
  public AppendEventResult append(Event event) {
    String partitionKey = getEventPartition(event);
    return serializer.serialize(event).flatMap((data) -> putRecord(partitionKey, data))
        .fold((e) -> {
          String errorMessage = e.getMessage();
          logger.error(errorMessage);
          return AppendEventResult.rejected(errorMessage);
        }, (ok) -> AppendEventResult.accepted());
  }

  private Try<Boolean> putRecord(String partitionKey, String serializedData) {
    SdkBytes data = SdkBytes.fromString(serializedData, Charset.forName("UTF-8"));
    PutRecordRequest request = PutRecordRequest.builder().streamName(this.streamName)
        .partitionKey(partitionKey).data(data).build();
    return Checked.now(() -> kinesisClient.putRecord(request)).map((r) -> true);
  }

  private String getEventPartition(Event event) {
    return String.format("%s:%s", event.namespace, event.metric);

  }
}
