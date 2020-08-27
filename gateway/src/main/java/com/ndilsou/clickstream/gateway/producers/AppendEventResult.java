package com.ndilsou.clickstream.gateway.producers;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.atlassian.fugue.Option;
import java.util.Optional;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class AppendEventResult {
  private final boolean accepted;
  final Optional<String> error;

  public AppendEventResult(boolean accepted) {
    this.accepted = accepted;
    this.error = Optional.empty();
  }

  public AppendEventResult(boolean accepted, Optional<String> error) {
    this.accepted = accepted;
    this.error = error;
  }

  public AppendEventResult(boolean accepted, Option<String> error) {
    this.accepted = accepted;
    this.error = error.toOptional();
  }

  public static AppendEventResult accepted() {
    return new AppendEventResult(true);
  }

  public static AppendEventResult rejected(String error) {
    return new AppendEventResult(false, Optional.of(error));
  }

  public boolean isAccepted() {
    return accepted;
  }



}
