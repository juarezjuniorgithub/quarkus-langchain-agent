package com.oracle.dev.quarkus.langchain4j.agent;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/agent")
public class TechSupportAgentResource {

  @Inject
  TechSupportAgent agent;

  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public String agent(String prompt) {
    return agent.chat(prompt);
  }
}