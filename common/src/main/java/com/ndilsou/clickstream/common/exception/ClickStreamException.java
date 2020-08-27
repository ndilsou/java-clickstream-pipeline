package com.ndilsou.clickstream.common.exception;

public class ClickStreamException extends RuntimeException {

  private static final long serialVersionUID = -8009452675738052436L;

  public ClickStreamException(String message) {
    super(message);
  }

  public ClickStreamException(String message, Throwable cause) {
    super(message, cause);
  }

}
