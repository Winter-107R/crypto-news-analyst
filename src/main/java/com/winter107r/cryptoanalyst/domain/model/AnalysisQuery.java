package com.winter107r.cryptoanalyst.domain.model;

import java.util.Objects;

/** Value object representing a user's risk-analysis question. */
public final class AnalysisQuery {

    private final String question;
    private final int topK;         // how many docs to retrieve for RAG context

    public AnalysisQuery(String question, int topK) {
        if (question == null || question.isBlank()) throw new IllegalArgumentException("question must not be blank");
        this.question = question.trim();
        this.topK     = topK > 0 ? topK : 3;
    }

    public AnalysisQuery(String question) { this(question, 3); }

    public String getQuestion() { return question; }
    public int getTopK()        { return topK; }

    /** Stable hash used as Redis cache key. */
    public String cacheKey() { return "insight:" + Integer.toHexString(question.toLowerCase().hashCode()); }

    @Override public boolean equals(Object o) { return o instanceof AnalysisQuery q && question.equals(q.question); }
    @Override public int hashCode()            { return Objects.hash(question); }
    @Override public String toString()         { return "AnalysisQuery{question=" + question + ", topK=" + topK + "}"; }
}
