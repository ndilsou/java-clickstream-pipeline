package com.ndilsou.clickstream.gateway;

import com.ndilsou.clickstream.common.events.Event;
import com.ndilsou.clickstream.gateway.producers.EventProducer;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {
  @Inject
  EventProducer eventProducer;

  @POST
  public Response add(Event event) {
    var result = eventProducer.append(event);
    Response response;
    if (result.isAccepted()) {
      response = Response.status(Status.ACCEPTED).entity(result).build();
    } else {
      response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
    }
    return response;
  }
}
