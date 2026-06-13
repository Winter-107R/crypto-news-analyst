package com.winter107r.cryptoanalyst.infrastructure.llm;

import com.winter107r.cryptoanalyst.domain.port.out.LlmPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Real LLM via OpenAI REST API (no Spring AI dependency needed).
 * Active when: --spring.profiles.active=openai + OPENAI_API_KEY env var set.
 */
@Component
@Profile("openai")
public class OpenAiLlmAdapter implements LlmPort {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final String       apiKey;
    private final String       model;

    public OpenAiLlmAdapter(
            RestTemplate restTemplate,
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model) {
        this.restTemplate = restTemplate;
        this.apiKey       = Objects.requireNonNull(apiKey, "openai.api-key must be set");
        this.model        = Objects.requireNonNull(model);
    }

    @Override
    public String generate(String question, List<String> contextChunks) {
        String context = contextChunks.stream()
                .map(c -> "---\n" + c)
                .collect(Collectors.joining("\n"));

        // System prompt instructs the LLM to stay grounded in retrieved context only.
        String systemPrompt = """
                You are a crypto market risk analyst.
                Use ONLY the provided news context to answer.
                If the context lacks enough information, say so explicitly.
                Do not hallucinate data or prices.""";

        String userMessage = "CONTEXT:\n" + context + "\n\nQUESTION: " + question;

        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.2,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user",   "content", userMessage)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        @SuppressWarnings({"unchecked", "null"})
        Map<String, Object> response = restTemplate.exchange(
                OPENAI_URL, HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        ).getBody();

        // Parse choices[0].message.content
        if (response != null) {
            var choices = (List<?>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                var choice  = (Map<?, ?>) choices.get(0);
                var message = (Map<?, ?>) choice.get("message");
                if (message != null) return (String) message.get("content");
            }
        }
        return "[No response from OpenAI]";
    }
}
