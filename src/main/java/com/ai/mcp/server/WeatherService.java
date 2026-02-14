package com.ai.mcp.server;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.*;
import org.springaicommunity.mcp.annotation.McpProgressToken;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springaicommunity.mcp.context.McpSyncRequestContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    public record WeatherResponse(Current current) {
        public record Current(LocalDateTime time, int interval, double temperature_2m) {}
    }

    @McpTool(description = "Get the temperature (in celsius) for a specific location")
    public String getTemperature(
            McpSyncServerExchange exchange,
            McpSyncRequestContext context,// (1)
            @McpToolParam(description = "The location latitude") double latitude,
            @McpToolParam(description = "The location longitude") double longitude,
            @McpProgressToken int progressToken) { // (2)

        exchange.loggingNotification(LoggingMessageNotification.builder() // (3)
                .level(LoggingLevel.DEBUG)
                .data("Call getTemperature Tool with latitude: " + latitude + " and longitude: " + longitude)
                .meta(Map.of()) // non null meta as a workaround for bug: ...
                .build());

        WeatherResponse weatherResponse = RestClient.create()
                .get()
                .uri("https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m",
                        latitude, longitude)
                .retrieve()
                .body(WeatherResponse.class);


        String epicPoem = "MCP Client doesn't provide sampling capability.";

        // Access progress token from context,
        // It will be an integer when using MCP Inspector v0.20.0
        Object pt = context.request().progressToken();

        if (exchange.getClientCapabilities().sampling() != null) {
            // 50% progress
            exchange.progressNotification(new ProgressNotification(progressToken, 0.5, 1.0, "Start sampling"));	// (4)

            String samplingMessage = """
					For a weather forecast (temperature is in Celsius): %s.
					At location with latitude: %s and longitude: %s.
					Please write an epic poem about this forecast using a Shakespearean style.
					""".formatted(weatherResponse.current().temperature_2m(), latitude, longitude);

            CreateMessageResult samplingResponse = exchange.createMessage(CreateMessageRequest.builder()
                    .systemPrompt("You are a poet!")
                    .maxTokens(100)
                    .messages(List.of(new McpSchema.SamplingMessage(McpSchema.Role.USER, new TextContent(samplingMessage))))
                    .build()); // (5)

            epicPoem = ((TextContent) samplingResponse.content()).text();
        }

        // 100% progress
        exchange.progressNotification(new ProgressNotification(progressToken, 1.0, 1.0, "Task completed"));

        return """
			Weather Poem: %s			
			about the weather: %s°C at location: (%s, %s)		
			""".formatted(epicPoem, weatherResponse.current().temperature_2m(), latitude, longitude);
    }
}
