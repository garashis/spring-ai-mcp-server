package com.ai.mcp.server;

import io.modelcontextprotocol.spec.McpSchema;
import org.springaicommunity.mcp.annotation.McpComplete;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompletionProvider {

    List<String> cities = List.of("London", "Manchester", "Gurgaon");

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


    @McpComplete(uri = "user-profile://{username}")
    public List<String> completeUsername(McpSchema.CompleteRequest.CompleteArgument argument) {
        List<String> usernames = List.of("username_copied", "fast_and_the_curious", "real_name_hidden");

        String prefix = argument.value().toLowerCase();
        String argumentName = argument.name();

        // Different completions based on argument name
        if ("username".equals(argumentName)) {
            return usernames.stream()
                    .filter(city -> city.toLowerCase().startsWith(prefix.toLowerCase()))
                    .limit(10)
                    .toList();
        } else {
            return List.of();
        }
    }


}