package com.winter107r.cryptoanalyst.application;

import com.winter107r.cryptoanalyst.application.usecase.IngestNewsService;
import com.winter107r.cryptoanalyst.domain.model.NewsArticle;
import com.winter107r.cryptoanalyst.domain.port.out.NewsRepository;
import com.winter107r.cryptoanalyst.domain.port.out.VectorStorePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestNewsServiceTest {

    @Mock NewsRepository  newsRepository;
    @Mock VectorStorePort vectorStore;

    IngestNewsService service;

    @BeforeEach
    void setUp() {
        service = new IngestNewsService(newsRepository, vectorStore);
    }

    private NewsArticle btcArticle() {
        return new NewsArticle("art-001", "BTC Surges Past $100k",
                               "Bitcoin broke the 100k milestone driven by ETF inflows.",
                               "CoinDesk", "BTC", Instant.now());
    }

    @Test
    @DisplayName("saves article and adds to vector store on first ingest")
    void ingestsNewArticle() {
        NewsArticle article = btcArticle();
        when(newsRepository.existsById(article.getId())).thenReturn(false);

        service.ingest(article);

        verify(newsRepository).save(article);
        verify(vectorStore).add(article);
    }

    @Test
    @DisplayName("skips duplicate article — idempotent on second ingest")
    void skipsDuplicateArticle() {
        NewsArticle article = btcArticle();
        when(newsRepository.existsById(article.getId())).thenReturn(true);

        service.ingest(article);

        verify(newsRepository, never()).save(any());
        verify(vectorStore, never()).add(any());
    }

    @Test
    @DisplayName("embedding text includes symbol prefix for vector retrieval")
    void embeddingTextIncludesSymbol() {
        NewsArticle article = btcArticle();
        String embedding = article.toEmbeddingText();

        assert embedding.startsWith("[BTC]") : "embedding must start with [symbol]";
        assert embedding.contains(article.getTitle()) : "embedding must include title";
    }
}
