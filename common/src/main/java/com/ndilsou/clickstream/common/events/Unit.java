package com.ndilsou.clickstream.common.events;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Unit {
  Count("Count"), Seconds("Seconds"), Bytes("Bytes");

  private final String unit;

  private Unit(String unit) {
    this.unit = unit;
  }

  @JsonValue
  public String toValue() {
    return unit;
  }
}
