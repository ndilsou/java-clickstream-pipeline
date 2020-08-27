package com.ndilsou.clickstream.common.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Event {
  public String namespace;
  public String metric;
  public OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);
  public Unit unit;
  public double value;
  public JsonNode dimensions;
}
