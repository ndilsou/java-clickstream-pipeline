package com.ndilsou.clickstream.processor;

import javax.inject.Singleton;
import com.ndilsou.clickstream.common.exception.ClickStreamException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.kinesis.KinesisClient;

@Singleton
public class KinesisSink {

  KinesisClient kinesisClient;
  private final Logger logger = LoggerFactory.getLogger(KinesisSink.class);
  private final String[] streamNames;

  KinesisSink(@ConfigProperty(name = "kinesis.stream-names") String[] streamNames) {
    this.streamNames = streamNames;
    this.kinesisClient = KinesisClient.create();
    for (String streamName : this.streamNames) {
      try {
        kinesisClient.describeStream(ds -> ds.streamName(streamName));

      } catch (Exception e) {
        throw new ClickStreamException(String.format("Kinesis Stream %s may not exist", streamName),
            e);
      }

    }
  }

  public void sink(TransformedEvent event) {

  }

}
