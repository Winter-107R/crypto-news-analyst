package com.winter107r.cryptoanalyst.infrastructure.llm;

import com.winter107r.cryptoanalyst.domain.port.out.LlmPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock LLM — active by default (no API key required).
 * Returns a templated response using retrieved context chunks.
 * Switch to real LLM: --spring.profiles.active=openai + OPENAI_API_KEY env var.
 */
@Component
@Profile({"default", "mock", "!openai"})
public class MockLlmAdapter implements LlmPort {

    @Override
    public String generate(String question, List<String> contextChunks) {
        if (contextChunks.isEmpty()) {
            return "[MOCK] No relevant news found for: \"" + question +
                   "\". Ingest some articles first via POST /api/news/ingest.";
        }
        String summary = contextChunks.stream()
                .limit(3)
                .map(c -> "• " + (c.length() > 120 ? c.substring(0, 120) + "..." : c))
                .collect(Collectors.joining("\n"));

        return "[MOCK RESPONSE — set profile=openai + OPENAI_API_KEY for real LLM]\n\n" +
               "Based on " + contextChunks.size() + " retrieved news articles:\n\n" +
               summary + "\n\n" +
               "Analysis: The retrieved context suggests activity around \"" + question +
               "\". Replace this adapter with OpenAiLlmAdapter for actual LLM-generated insights.";
    }
}
