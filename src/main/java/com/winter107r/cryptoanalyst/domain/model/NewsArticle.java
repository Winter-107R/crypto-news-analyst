package com.winter107r.cryptoanalyst.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain value object — pure Java, zero framework dependencies.
 * Immutable by design; equality is content-based (title + source + publishedAt).
 */
public final class NewsArticle {

    private final String id;
    private final String title;
    private final String content;
    private final String source;
    private final String symbol;        // e.g. "BTC", "ETH", "ALL"
    private final Instant publishedAt;

    public NewsArticle(String id, String title, String content,
                       String source, String symbol, Instant publishedAt) {
        this.id          = Objects.requireNonNull(id, "id");
        this.title       = Objects.requireNonNull(title, "title");
        this.content     = Objects.requireNonNull(content, "content");
        this.source      = Objects.requireNonNull(source, "source");
        this.symbol      = symbol != null ? symbol.toUpperCase() : "ALL";
        this.publishedAt = publishedAt != null ? publishedAt : Instant.now();
    }

    public String getId()          { return id; }
    public String getTitle()       { return title; }
    public String getContent()     { return content; }
    public String getSource()      { return source; }
    public String getSymbol()      { return symbol; }
    public Instant getPublishedAt(){ return publishedAt; }

    /** Full text used for embedding / keyword search. */
    public String toEmbeddingText() {
        return "[" + symbol + "] " + title + "\n" + content;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NewsArticle n)) return false;
        return title.equals(n.title) && source.equals(n.source) && publishedAt.equals(n.publishedAt);
    }

    @Override public int hashCode() { return Objects.hash(title, source, publishedAt); }
    @Override public String toString() { return "NewsArticle{id=" + id + ", symbol=" + symbol + ", title=" + title + "}"; }
}
