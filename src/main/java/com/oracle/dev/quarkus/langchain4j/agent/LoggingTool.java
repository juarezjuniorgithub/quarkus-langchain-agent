package com.oracle.dev.quarkus.langchain4j.agent;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoggingTool {

  @Tool("Given and error code, only if an email message has not been sent to customer@buildstuff.events, logs the corresponding error description as a log message")
  public void logErrorDescription(String logMessage) {
    System.out.println("--- Logging message: " + logMessage + "---");
  }

}
