package com.ai.mcp.server;

import io.modelcontextprotocol.spec.McpSchema;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springaicommunity.mcp.context.McpSyncRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ResourceProvider {

    @Value("#{${app.config}}")
    private Map<String, String> config;

    @Value("#{${app.boolconfig}}")
    private Map<String, Boolean> boolConfig;

    //Basic Usage
    @McpResource(
            uri = "config://{key}",
            name = "Configuration",
            description = "Provides configuration data")
    public String getConfig(String key) {
        return config.get(key);
    }

    //With ReadResourceResult
    @McpResource(
            uri = "user-profile://{username}",
            name = "User Profile",
            description = "Provides user profile information")
    public McpSchema.ReadResourceResult getUserProfile(@McpArg(name = "User name", description = "User name to be searched") String username) {

        // Load profile data from external system
        //  String profileData = loadUserProfile(username);

        String profileData = """
                Alex Rivers is a dedicated software architect based in Seattle, specializing in scalable cloud infrastructure. 
                With over a decade of experience, Alex enjoys mentoring junior developers and contributing to open-source projects. 
                Outside of work, you can find them hiking mountain trails or experimenting with artisan sourdough bread recipes at home
                """;

        return new McpSchema.ReadResourceResult(List.of(
                new McpSchema.TextResourceContents(
                        "user-profile://" + username,
                        "application/json",
                        profileData)
        ));
    }

    @McpResource(
            uri = "data://{id}",
            name = "Data Resource",
            description = "Resource with request context")
    public McpSchema.ReadResourceResult getData(
            McpSyncRequestContext context,
            String id) {

        // Send logging notification using convenient method
        context.info("Accessing resource: " + id);

        // Ping the client
        context.ping();

//        String data = fetchData(id);
        String data = """
                Jordan Case is a versatile marketing strategist from London, passionate about digital storytelling. 
                Jordan excels at building brand identities and engaging social media campaigns. 
                When not working, they enjoy urban photography and exploring local coffee shops, always seeking fresh inspiration for their next creative project and documenting the city's hidden gems along the way.
                """;

        return new McpSchema.ReadResourceResult(List.of(
                new McpSchema.TextResourceContents("data://" + id, "text/plain", data)
        ));
    }
}