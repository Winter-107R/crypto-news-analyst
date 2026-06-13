package com.winter107r.cryptoanalyst.infrastructure.persistence.vector;

import com.winter107r.cryptoanalyst.domain.model.NewsArticle;
import com.winter107r.cryptoanalyst.domain.port.out.NewsRepository;
import com.winter107r.cryptoanalyst.domain.port.out.VectorStorePort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * VectorStorePort backed by MySQL keyword search instead of real embeddings.
 * Works without any API key. Swap to a proper vector DB by swapping the profile.
 */
@Component
public class KeywordVectorAdapter implements VectorStorePort {

    private final NewsRepository newsRepository;

    public KeywordVectorAdapter(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public void add(NewsArticle article) {
        // no-op: article is already in MySQL from IngestNewsService
    }

    @Override
    public List<String> similaritySearch(String query, int topK) {
        String keyword = extractKeyword(query);
        return newsRepository.findByKeyword(keyword, topK).stream()
                .map(NewsArticle::toEmbeddingText)
                .collect(Collectors.toList());
    }

    /** Returns the first meaningful word from the query, skipping stop words. */
    private String extractKeyword(String query) {
        List<String> stopWords = List.of("what", "are", "the", "for", "this", "that",
                "with", "from", "have", "will", "been", "week", "risk", "risks");
        return Arrays.stream(query.toLowerCase().split("\\W+"))
                .filter(w -> w.length() > 3 && !stopWords.contains(w))
                .findFirst()
                .orElse(query.split(" ")[0]); // fallback: first word of query
    }
}
