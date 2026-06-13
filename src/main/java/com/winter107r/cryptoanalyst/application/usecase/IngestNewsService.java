package com.winter107r.cryptoanalyst.application.usecase;

import com.winter107r.cryptoanalyst.domain.model.NewsArticle;
import com.winter107r.cryptoanalyst.domain.port.in.IngestNewsUseCase;
import com.winter107r.cryptoanalyst.domain.port.out.NewsRepository;
import com.winter107r.cryptoanalyst.domain.port.out.VectorStorePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Saves a news article to MySQL then indexes it for RAG retrieval.
 * Idempotent — duplicate IDs are skipped (Kafka at-least-once delivery).
 */
public class IngestNewsService implements IngestNewsUseCase {

    private static final Logger log = LoggerFactory.getLogger(IngestNewsService.class);

    private final NewsRepository  newsRepository;
    private final VectorStorePort vectorStore;

    public IngestNewsService(NewsRepository newsRepository, VectorStorePort vectorStore) {
        this.newsRepository = newsRepository;
        this.vectorStore    = vectorStore;
    }

    @Override
    public void ingest(NewsArticle article) {
        if (newsRepository.existsById(article.getId())) {
            log.debug("Skipping duplicate article: {}", article.getId());
            return;
        }
        newsRepository.save(article);
        vectorStore.add(article);
        log.info("Ingested article [{}] symbol={}", article.getId(), article.getSymbol());
    }
}
