package com.ai.mcp.server;

import io.modelcontextprotocol.spec.McpSchema;
import static io.modelcontextprotocol.spec.McpSchema.*;
import org.springaicommunity.mcp.annotation.McpArg;
import static org.springaicommunity.mcp.annotation.McpPrompt.*;

import org.springaicommunity.mcp.annotation.McpPrompt;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptProvider {

    @McpPrompt(
            name = "greeting",
            description = "Generate a greeting message")
    public GetPromptResult greeting(@McpArg(name = "name", description = "User's name", required = true) String name) {

        String message = "Hello, " + name + "! How can I help you today?";

        return new GetPromptResult(
                "Greeting",
                List.of(new PromptMessage(Role.ASSISTANT, new TextContent(message)))
        );
    }

    @McpPrompt(
            name = "personalized-greeting-message",
            title = "personalized message",
            description = "Generate a personalized message")
    public GetPromptResult personalizedMessage(
            @McpArg(name = "name", required = true) String name,
            @McpArg(name = "age", required = false) Integer age,
            @McpArg(name = "city", required = false) String city) {

        StringBuilder message = new StringBuilder();
        message.append("Hello, ").append(name).append("!\n\n");

        if (age != null) {
            message.append("At ").append(age).append(" years old, ");
            // Add age-specific content
        }

        if (city != null && !city.isEmpty()) {
            message.append("Your interest in ").append(city);
            // Add interest-specific content
        }

        return new GetPromptResult(
                "Personalized Message",
                List.of(new PromptMessage(Role.ASSISTANT, new TextContent(message.toString())))
        );
    }
}
