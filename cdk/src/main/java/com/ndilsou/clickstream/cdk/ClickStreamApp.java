package com.ndilsou.clickstream.cdk;

import software.amazon.awscdk.core.App;


public class ClickStreamApp {
  public static void main(final String[] args) {
    App app = new App();

    new ClickStreamStack(app, "ClickStreamStack");

    app.synth();
  }
}
