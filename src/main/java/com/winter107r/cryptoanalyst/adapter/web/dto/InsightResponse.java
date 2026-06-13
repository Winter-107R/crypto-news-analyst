package com.winter107r.cryptoanalyst.adapter.web.dto;

import com.winter107r.cryptoanalyst.domain.model.RiskInsight;

import java.time.Instant;
import java.util.List;

public record InsightResponse(
        String id,
        String question,
        String answer,
        List<String> sources,
        boolean cached,
        Instant generatedAt
) {
    public static InsightResponse from(RiskInsight insight) {
        return new InsightResponse(
                insight.getId(), insight.getQuestion(), insight.getAnswer(),
                insight.getSourceTitles(), insight.isCached(), insight.getGeneratedAt());
    }
}
