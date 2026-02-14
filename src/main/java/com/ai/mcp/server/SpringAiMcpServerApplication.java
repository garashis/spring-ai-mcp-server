package com.ai.mcp.server;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableAsync
public class SpringAiMcpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiMcpServerApplication.class, args);
	}

//	@Bean
//	public List<McpServerFeatures.AsyncCompletionSpecification> getCitySuggestions(Map<McpSchema.CompleteReference, McpServerFeatures.AsyncCompletionSpecification> completions
//) {
//		var completion = new McpServerFeatures.AsyncCompletionSpecification(
//				new McpSchema.PromptReference(
//						"ref/prompt", "personalized-message-for-city-search", "Generate a personalized message"),
//				(exchange, request) -> {
//					// Logic to generate suggestions based on request.argument()
//					// Here we simulate fetching dynamic data
//					List<String> suggestions = List.of("fix_typo", "security_check", "performance_review");
//
//					// Return results asynchronously using Project Reactor (Mono)
//					return Mono.just(new McpSchema.CompleteResult(
//							new McpSchema.CompleteResult.CompleteCompletion(suggestions,3, false)
//
//					));
//				}
//		);
//
//		//completions.put(List.of(completion));
//		return List.of(completion);
//	}


}
