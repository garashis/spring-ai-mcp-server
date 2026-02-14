package com.ai.mcp.server;

import io.modelcontextprotocol.spec.McpSchema;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpComplete;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompletionProvider {

    List<String> cities = List.of("London", "Manchester", "Bangalore");

    //    @McpComplete(prompt = "personalized-greeting-message")
    public List<String> completeCityName(String prefix) {
        return cities.stream()
                .filter(city -> city.toLowerCase().startsWith(prefix.toLowerCase()))
                .limit(10)
                .toList();
    }


    @McpComplete(prompt = "personalized-greeting-message")
    public List<String> completeTravelDestination(McpSchema.CompleteRequest.CompleteArgument argument) {
        String prefix = argument.value().toLowerCase();
        String argumentName = argument.name();

        // Different completions based on argument name
        if ("city".equals(argumentName)) {
            return cities.stream()
                    .filter(city -> city.toLowerCase().startsWith(prefix.toLowerCase()))
                    .limit(10)
                    .toList();
        } else {
            return List.of();
        }
    }
}