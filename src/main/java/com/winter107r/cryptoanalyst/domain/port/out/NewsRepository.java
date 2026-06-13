package com.winter107r.cryptoanalyst.domain.port.out;

import com.winter107r.cryptoanalyst.domain.model.NewsArticle;
import java.util.List;

/** Outbound port — implemented by MySQL adapter in infrastructure layer. */
public interface NewsRepository {
    void save(NewsArticle article);
    List<NewsArticle> findByKeyword(String keyword, int limit);
    boolean existsById(String id);
}
