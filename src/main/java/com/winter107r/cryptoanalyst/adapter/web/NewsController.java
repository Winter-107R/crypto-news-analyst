package com.winter107r.cryptoanalyst.adapter.web;

import com.winter107r.cryptoanalyst.adapter.web.dto.IngestRequest;
import com.winter107r.cryptoanalyst.domain.model.NewsArticle;
import com.winter107r.cryptoanalyst.infrastructure.messaging.kafka.NewsKafkaProducer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Inbound adapter — REST entry point for news ingestion.
 * Publishes to Kafka; the consumer triggers IngestNewsUseCase asynchronously.
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsKafkaProducer producer;

    public NewsController(NewsKafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, String>> ingest(@Valid @RequestBody IngestRequest req) {
        String id = UUID.randomUUID().toString();
        NewsArticle article = new NewsArticle(
                id, req.title(), req.content(), req.source(),
                req.symbol() != null ? req.symbol() : "ALL", Instant.now());
        producer.publish(article);
        return ResponseEntity.accepted().body(Map.of("id", id, "status", "queued"));
    }
}
