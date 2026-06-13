package com.winter107r.cryptoanalyst.domain.port.out;

import com.winter107r.cryptoanalyst.domain.model.NewsArticle;
import java.util.List;

/**
 * Outbound port for semantic similarity search.
 * Default impl: keyword search on MySQL (no API key required).
 * Production impl: Spring AI VectorStore + OpenAI embeddings (swap via Spring profile).
 */
public interface VectorStorePort {
    void add(NewsArticle article);
    List<String> similaritySearch(String query, int topK);
}
