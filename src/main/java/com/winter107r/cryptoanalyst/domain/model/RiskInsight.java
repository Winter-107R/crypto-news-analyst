package com.winter107r.cryptoanalyst.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Domain entity — the generated risk analysis answer with source attribution. */
public final class RiskInsight {

    private final String id;
    private final String question;
    private final String answer;
    private final List<String> sourceTitles;   // titles of retrieved news used as context
    private final boolean cached;
    private final Instant generatedAt;

    public RiskInsight(String id, String question, String answer,
                       List<String> sourceTitles, boolean cached, Instant generatedAt) {
        this.id           = Objects.requireNonNull(id);
        this.question     = Objects.requireNonNull(question);
        this.answer       = Objects.requireNonNull(answer);
        this.sourceTitles = sourceTitles != null ? List.copyOf(sourceTitles) : List.of();
        this.cached       = cached;
        this.generatedAt  = generatedAt != null ? generatedAt : Instant.now();
    }

    public String getId()                 { return id; }
    public String getQuestion()           { return question; }
    public String getAnswer()             { return answer; }
    public List<String> getSourceTitles() { return sourceTitles; }
    public boolean isCached()             { return cached; }
    public Instant getGeneratedAt()       { return generatedAt; }

    public RiskInsight asCached() {
        return new RiskInsight(id, question, answer, sourceTitles, true, generatedAt);
    }

    @Override public String toString() {
        return "RiskInsight{id=" + id + ", cached=" + cached + ", sources=" + sourceTitles.size() + "}";
    }
}
