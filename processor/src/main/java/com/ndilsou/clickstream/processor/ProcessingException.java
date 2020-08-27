package com.ndilsou.clickstream.processor;

public class ProcessingException extends RuntimeException {
  private static final long serialVersionUID = 102026628597819382L;

  private Object source;
  private String stage;

  public ProcessingException(String errorMessage, Throwable err, Object source) {
    super(errorMessage, err);
    this.source = source;
  }

  public ProcessingException(String errorMessage, Object source) {
    super(errorMessage);
    this.source = source;
  }


  public ProcessingException(Throwable err, Object source) {
    super(err);
    this.source = source;
  }

  public Object getSource() {
    return source;
  }

  public String getStage() {
    return stage;
  }


}
