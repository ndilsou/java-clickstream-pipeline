package com.ndilsou.clickstream.cdk;

import java.util.Map;
import java.util.Optional;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.TaskDefinition;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.events.targets.KinesisStream;
import software.amazon.awscdk.services.kinesis.Stream;

public class ClickStreamStack extends Stack {

  /**
   * Defines the stack for the ClickStream application.
   *
   * @param scope parent scope
   * @param id    identifier of the construct
   */
  public ClickStreamStack(final Construct scope, final String id) {
    super(scope, id);
    String environment =
        (String) Optional.ofNullable(this.getNode().tryGetContext("environment")).orElse("dev");



    var omnibusEventStream = Stream.Builder.create(this, "OmnibusEventStream")
        .streamName(String.format("clickstream-%s", environment)).shardCount(1)
        .retentionPeriod(Duration.days(1)).build();


    var gatewayTaskDefinition =
        FargateTaskDefinition.Builder.create(this, "GatewayTaskDefinition")..build();
    var gatewayFargateService = ApplicationLoadBalancedFargateService.Builder
        .create(this, "GatewayFargateService").assignPublicIp(true).listenerPort(8080)
        .serviceName(String.format("clickstream-gateway-%s", environment))
        .taskDefinition(gatewayTaskDefinition).build();


  }
}
