package com.oracle.dev.quarkus.langchain4j.agent;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailTool {

  @Inject
  Mailer mailer;

  @Tool("Sends an email to a specified recipient using the error code as subject and the respective error description as the email text body")
  public String sendEmail(String recipient, String subject, String body) {
    mailer.send(Mail.withText(recipient, subject, body));
    return jakarta.ws.rs.core.Response.Status.OK.name();
  }
}