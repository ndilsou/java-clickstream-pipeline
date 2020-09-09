package com.ndilsou.clickstream.common.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Event {

  private String namespace;

  private String metric;

  private OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);

  private Unit unit = Unit.Count;

  private double value = 1.0;

  private JsonNode dimensions = JsonNodeFactory.instance.objectNode();

  @JsonProperty("ip_address")
  private String ipAddress;

  @JsonProperty("user_agent")
  private String userAgent;

  @JsonProperty("created_at")
  private OffsetDateTime createdAt;

}
