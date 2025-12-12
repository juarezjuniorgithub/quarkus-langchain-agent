package com.oracle.dev.quarkus.langchain4j.agent;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(tools = { EmailTool.class, OracleDatabaseTool.class, LoggingTool.class })
public interface TechSupportAgent {

  String chat(@UserMessage String message);

}
