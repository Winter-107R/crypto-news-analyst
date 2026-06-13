package com.winter107r.cryptoanalyst.domain.port.out;

import java.util.List;

/**
 * Outbound port for LLM text generation.
 * Mock impl: template response (no API key).
 * OpenAI impl: Spring AI ChatClient (set OPENAI_API_KEY + profile=openai).
 */
public interface LlmPort {
    String generate(String question, List<String> contextChunks);
}
