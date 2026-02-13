package com.ai.mcp.server;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

    @McpTool(name = "add", description = "Add two numbers together")
    public int add(
            @McpToolParam(description = "First number") int a,
            @McpToolParam(description = "Second number") int b) {
        return a + b;
    }

//    @McpResource(uri = "config://{key}", name = "Configuration")
//    public String getConfig(String key) {
//        return configData.get(key);
//    }
}
