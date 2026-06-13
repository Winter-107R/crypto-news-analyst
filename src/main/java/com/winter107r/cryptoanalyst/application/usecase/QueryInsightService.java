package com.winter107r.cryptoanalyst.application.usecase;

import com.winter107r.cryptoanalyst.domain.model.AnalysisQuery;
import com.winter107r.cryptoanalyst.domain.model.RiskInsight;
import com.winter107r.cryptoanalyst.domain.port.in.QueryInsightUseCase;
import com.winter107r.cryptoanalyst.domain.port.out.CachePort;
import com.winter107r.cryptoanalyst.domain.port.out.LlmPort;
import com.winter107r.cryptoanalyst.domain.port.out.VectorStorePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * RAG query pipeline: cache check → news retrieval → LLM generation → cache write.
 * Kept Spring-free so unit tests can construct it directly without an application context.
 */
public class QueryInsightService implements QueryInsightUseCase {

    private static final Logger log = LoggerFactory.getLogger(QueryInsightService.class);

    private final VectorStorePort vectorStore; // finds relevant news chunks
    private final LlmPort         llm;         // generates the answer
    private final CachePort       cache;       // avoids re-calling LLM for same question

    public QueryInsightService(VectorStorePort vectorStore, LlmPort llm, CachePort cache) {
        this.vectorStore = vectorStore;
        this.llm         = llm;
        this.cache       = cache;
    }

    @Override
    public RiskInsight query(AnalysisQuery query) {
        // 1. Already answered this recently? Return cached result, skip LLM.
        Optional<RiskInsight> cached = cache.get(query.cacheKey());
        if (cached.isPresent()) {
            log.debug("Cache HIT for key={}", query.cacheKey());
            return cached.get().asCached();
        }

        // 2. Find the most relevant news articles for this question.
        List<String> contextChunks = vectorStore.similaritySearch(query.getQuestion(), query.getTopK());
        log.debug("Retrieved {} chunks for question='{}'", contextChunks.size(), query.getQuestion());

        // 3. Ask LLM to answer using only those articles as context.
        String answer = llm.generate(query.getQuestion(), contextChunks);

        // 4. Build result. Truncate source snippets to 80 chars for display.
        List<String> sourceSummaries = contextChunks.stream()
                .map(chunk -> chunk.length() > 80 ? chunk.substring(0, 80) + "..." : chunk)
                .toList();

        RiskInsight insight = new RiskInsight(
                UUID.randomUUID().toString(),
                query.getQuestion(),
                answer,
                sourceSummaries,
                false,
                Instant.now()
        );
        cache.put(query.cacheKey(), insight);
        return insight;
    }
}
