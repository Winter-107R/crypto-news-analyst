package com.winter107r.cryptoanalyst.infrastructure.persistence.mysql;

import com.winter107r.cryptoanalyst.domain.model.NewsArticle;
import com.winter107r.cryptoanalyst.domain.port.out.NewsRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/** Implements domain NewsRepository port using JPA + MySQL. */
@Component
public class MySqlNewsAdapter implements NewsRepository {

    private final NewsJpaRepositoryJpa jpa;

    public MySqlNewsAdapter(NewsJpaRepositoryJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(NewsArticle article) {
        jpa.save(new NewsJpaEntity(
                article.getId(), article.getTitle(), article.getContent(),
                article.getSource(), article.getSymbol(), article.getPublishedAt()));
    }

    @Override
    public List<NewsArticle> findByKeyword(String keyword, int limit) {
        return jpa.searchByKeyword(keyword).stream()
                .limit(limit)
                .map(e -> new NewsArticle(e.getId(), e.getTitle(), e.getContent(),
                                          e.getSource(), e.getSymbol(), e.getPublishedAt()))
                .toList();
    }

    @Override
    public boolean existsById(String id) {
        return jpa.existsById(Objects.requireNonNull(id));
    }
}
