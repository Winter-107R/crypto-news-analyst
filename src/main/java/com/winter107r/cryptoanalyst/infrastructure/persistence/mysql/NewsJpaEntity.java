package com.winter107r.cryptoanalyst.infrastructure.persistence.mysql;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "news_articles",
       indexes = { @Index(name = "idx_symbol", columnList = "symbol"),
                   @Index(name = "idx_published_at", columnList = "published_at") })
class NewsJpaEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 128)
    private String source;

    @Column(nullable = false, length = 16)
    private String symbol;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    protected NewsJpaEntity() {}

    NewsJpaEntity(String id, String title, String content,
                  String source, String symbol, Instant publishedAt) {
        this.id          = id;
        this.title       = title;
        this.content     = content;
        this.source      = source;
        this.symbol      = symbol;
        this.publishedAt = publishedAt;
    }

    String getId()          { return id; }
    String getTitle()       { return title; }
    String getContent()     { return content; }
    String getSource()      { return source; }
    String getSymbol()      { return symbol; }
    Instant getPublishedAt(){ return publishedAt; }
}
