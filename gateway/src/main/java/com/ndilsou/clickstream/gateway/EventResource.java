package com.ndilsou.clickstream.gateway;

import com.ndilsou.clickstream.common.events.Event;
import com.ndilsou.clickstream.gateway.producers.AppendEventResult;
import com.ndilsou.clickstream.gateway.producers.EventProducer;
import io.atlassian.fugue.Try;
import io.vertx.core.http.HttpServerRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;


@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {
  @Inject
  EventProducer eventProducer;

  @Context
  UriInfo info;

  @Context
  HttpServerRequest request;

  /**
   * Ingest a new event.
   *
   * @param event a piece of information you want recorded.
   * @return
   */
  @POST
  public Response add(Event event) {

    AppendEventResult result =
        validateEvent(event).map(this::enrichEvent).map(eventProducer::append)
            .fold(e -> AppendEventResult.rejected(e.getMessage()), res -> res);
    // var validEvent = validateEvent(event);
    // var enrichedEvent = enrichEvent(validEvent);
    // var result = eventProducer.append(enrichedEvent);

    Response response;
    if (result.isAccepted()) {
      response = Response.status(Status.ACCEPTED).entity(result).build();
    } else {
      response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
    }
    return response;
  }

  private Event enrichEvent(Event event) {
    // TODO handle X-Forwarded-For when behind load balancer.
    event.setIpAddress(request.remoteAddress().toString());
    event.setUserAgent(request.headers().get("User-Agent"));
    event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
    return event;
  }

  private Try<Event> validateEvent(Event event) {
    return Try.successful(event);
  }


}
