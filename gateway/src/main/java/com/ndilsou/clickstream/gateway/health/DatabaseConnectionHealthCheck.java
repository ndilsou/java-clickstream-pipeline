package com.ndilsou.clickstream.gateway.health;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class DatabaseConnectionHealthCheck implements HealthCheck {

  @ConfigProperty(name = "database.up", defaultValue = "false")
  private boolean databaseUp;

  @Override
  public HealthCheckResponse call() {
    HealthCheckResponseBuilder responseBuilder =
        HealthCheckResponse.named("Database connection health check");

    try {
      simulateDatabaseConnectionVerification();
      responseBuilder.up();
    } catch (IllegalStateException e) {
      responseBuilder.down().withData("error", e.getMessage());
    }

    return responseBuilder.build();

  }

  private void simulateDatabaseConnectionVerification() {
    if (!databaseUp) {
      throw new IllegalStateException("Cannot connect to database");
    }
  }

}
