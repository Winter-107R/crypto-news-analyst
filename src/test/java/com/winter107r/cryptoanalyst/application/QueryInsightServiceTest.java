package com.winter107r.cryptoanalyst.application;

import com.winter107r.cryptoanalyst.application.usecase.QueryInsightService;
import com.winter107r.cryptoanalyst.domain.model.AnalysisQuery;
import com.winter107r.cryptoanalyst.domain.model.RiskInsight;
import com.winter107r.cryptoanalyst.domain.port.out.CachePort;
import com.winter107r.cryptoanalyst.domain.port.out.LlmPort;
import com.winter107r.cryptoanalyst.domain.port.out.VectorStorePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryInsightServiceTest {

    @Mock VectorStorePort vectorStore;
    @Mock LlmPort         llm;
    @Mock CachePort       cache;

    QueryInsightService service;

    @BeforeEach
    void setUp() {
        service = new QueryInsightService(vectorStore, llm, cache);
    }

    @Test
    @DisplayName("returns cached insight without calling LLM on cache hit")
    void returnsCachedInsightOnHit() {
        AnalysisQuery query = new AnalysisQuery("What are BTC risks?");
        RiskInsight   cached = new RiskInsight("id1", query.getQuestion(), "cached answer",
                                               List.of(), true, Instant.now());
        when(cache.get(query.cacheKey())).thenReturn(Optional.of(cached));

        RiskInsight result = service.query(query);

        assertThat(result.isCached()).isTrue();
        assertThat(result.getAnswer()).isEqualTo("cached answer");
        verifyNoInteractions(vectorStore, llm);
    }

    @Test
    @DisplayName("calls vector store and LLM on cache miss, then caches result")
    void callsRagPipelineOnCacheMiss() {
        AnalysisQuery query   = new AnalysisQuery("What are ETH risks?");
        List<String>  chunks  = List.of("ETH gas fees surged 200%", "Ethereum merge impact analysis");
        when(cache.get(query.cacheKey())).thenReturn(Optional.empty());
        when(vectorStore.similaritySearch(query.getQuestion(), 3)).thenReturn(chunks);
        when(llm.generate(eq(query.getQuestion()), eq(chunks))).thenReturn("ETH shows volatility");

        RiskInsight result = service.query(query);

        assertThat(result.isCached()).isFalse();
        assertThat(result.getAnswer()).isEqualTo("ETH shows volatility");
        assertThat(result.getSourceTitles()).hasSize(2);
        verify(cache).put(eq(query.cacheKey()), any());
    }

    @Test
    @DisplayName("returns insight with empty sources when no context found")
    void handlesEmptyContextGracefully() {
        AnalysisQuery query = new AnalysisQuery("obscure coin risk");
        when(cache.get(any())).thenReturn(Optional.empty());
        when(vectorStore.similaritySearch(any(), anyInt())).thenReturn(List.of());
        when(llm.generate(any(), eq(List.of()))).thenReturn("No relevant data found.");

        RiskInsight result = service.query(query);

        assertThat(result.getSourceTitles()).isEmpty();
        assertThat(result.getAnswer()).contains("No relevant data");
    }

    @Test
    @DisplayName("cache key is stable for same question")
    void cacheKeyIsStable() {
        AnalysisQuery q1 = new AnalysisQuery("What are BTC risks?");
        AnalysisQuery q2 = new AnalysisQuery("What are BTC risks?");
        assertThat(q1.cacheKey()).isEqualTo(q2.cacheKey());
    }
}
