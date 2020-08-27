package com.ndilsou.clickstream.gateway.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ndilsou.clickstream.common.events.Event;

import io.atlassian.fugue.Checked;
import io.atlassian.fugue.Try;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JsonEventSerializer implements EventSerializer {
  private static final ObjectWriter OBJECT_WRITER = new ObjectMapper().writerFor(Event.class);

  @Override
  public Try<String> serialize(Event record) {
    return Checked.now(() -> OBJECT_WRITER.writeValueAsString(record));
  }
}
