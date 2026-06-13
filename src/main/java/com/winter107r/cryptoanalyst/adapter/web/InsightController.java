package com.winter107r.cryptoanalyst.adapter.web;

import com.winter107r.cryptoanalyst.adapter.web.dto.InsightResponse;
import com.winter107r.cryptoanalyst.adapter.web.dto.QueryRequest;
import com.winter107r.cryptoanalyst.domain.model.AnalysisQuery;
import com.winter107r.cryptoanalyst.domain.port.in.QueryInsightUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Inbound adapter — REST entry point for RAG insight queries.
 * Calls QueryInsightUseCase (cache → RAG → LLM).
 */
@RestController
@RequestMapping("/api/insights")
public class InsightController {

    private final QueryInsightUseCase queryInsightUseCase;

    public InsightController(QueryInsightUseCase queryInsightUseCase) {
        this.queryInsightUseCase = queryInsightUseCase;
    }

    @PostMapping("/query")
    public ResponseEntity<InsightResponse> query(@Valid @RequestBody QueryRequest req) {
        int topK = req.topK() != null ? req.topK() : 3;
        AnalysisQuery query   = new AnalysisQuery(req.question(), topK);
        InsightResponse resp  = InsightResponse.from(queryInsightUseCase.query(query));
        return ResponseEntity.ok(resp);
    }
}
