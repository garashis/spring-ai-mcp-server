package com.ai.mcp.server;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
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
import java.util.Objects;

record Location(@JsonPropertyDescription("The location latitude")
                @JsonFormat(shape = JsonFormat.Shape.NUMBER) String latitude,
                @JsonPropertyDescription("The longitude latitude") String longitude) {
}

@Service
public class WeatherService {


    @McpTool(description = "Get the temperature (in Celsius) for a specific location")
    public String getTemperature(
            McpSyncServerExchange exchange,
            McpSyncRequestContext context,// (1)
            @McpToolParam(description = "The location latitude") double latitude,
            @McpToolParam(description = "The location longitude") double longitude,
            @McpProgressToken int progressToken) { // (2)

        // Create an elicitation request
        McpSchema.ElicitRequest request = McpSchema.ElicitRequest.builder()
                .message("Consent to fetch the temperature for this location")
                .requestedSchema(
                        Map.of("type", "object", "required",
                                List.of("latitude", "longitude"), "properties",
                                Map.of(
                                        "latitude", Map.of("type", "number", "description", "The latitude in degrees", "default", latitude, "minimum", -90, "maximum", 90),
                                        "longitude", Map.of("type", "number", "description", "The longitude in degrees", "default", longitude, "minimum", -180, "maximum", 180))))
                .build();

        // Request elicitation from the client
        McpSchema.ElicitResult result = exchange.createElicitation(request);

        // Process the result
        ElicitResult.Action action = result.action();
        //Location location = context.elicit(Location.class).structuredContent();
        if (action.equals(ElicitResult.Action.ACCEPT)) {
            Map<String, Object> answer = result.content();
            if (answer.containsKey("latitude") && Objects.nonNull(answer.get("latitude"))) {
                latitude = Double.parseDouble(answer.get("latitude").toString());
            }
            if (answer.containsKey("longitude") && Objects.nonNull(answer.get("longitude"))) {
                longitude = Double.parseDouble(answer.get("longitude").toString());
            }
        }
        context.debug("Debug: Call getTemperature Tool with latitude: " + latitude + " and longitude: " + longitude);
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
            exchange.progressNotification(new ProgressNotification(progressToken, 0.5, 1.0, "Start sampling"));    // (4)

            String samplingMessage = """
                    For a weather forecast (temperature is in Celsius): %s.
                    At location with latitude: %s and longitude: %s.
                    Please write an epic poem about this forecast using a Shakespearean style.
                    """.formatted(weatherResponse.current().temperature_2m(), latitude, longitude);

            CreateMessageResult samplingResponse = exchange.createMessage(CreateMessageRequest.builder()
                    .modelPreferences(ModelPreferences.builder().addHint("openai").build())
                    .systemPrompt("You are a poet!")
                    .maxTokens(100)
                    .messages(List.of(new McpSchema.SamplingMessage(McpSchema.Role.USER, new TextContent(samplingMessage))))
                    .build()); // (5)

            epicPoem = ((TextContent) samplingResponse.content()).text();
        }

        // Ping the client
        context.ping();
        // Access progress token from context

        // 100% progress
        exchange.progressNotification(new ProgressNotification(progressToken, 1.0, 1.0, "Task completed"));

        return """
                Weather Poem: %s			
                about the weather: %s°C at location: (%s, %s)		
                """.formatted(epicPoem, weatherResponse.current().temperature_2m(), latitude, longitude);
    }

    public record WeatherResponse(Current current) {
        public record Current(LocalDateTime time, int interval, double temperature_2m) {
        }
    }
}
