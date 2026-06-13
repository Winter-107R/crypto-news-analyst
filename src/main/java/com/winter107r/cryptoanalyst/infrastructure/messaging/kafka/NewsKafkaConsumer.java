package com.winter107r.cryptoanalyst.infrastructure.messaging.kafka;

import com.winter107r.cryptoanalyst.domain.model.NewsArticle;
import com.winter107r.cryptoanalyst.domain.port.in.IngestNewsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Consumes raw news events from Kafka and delegates to IngestNewsUseCase. */
@Component
public class NewsKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(NewsKafkaConsumer.class);

    private final IngestNewsUseCase ingestNewsUseCase;

    public NewsKafkaConsumer(IngestNewsUseCase ingestNewsUseCase) {
        this.ingestNewsUseCase = ingestNewsUseCase;
    }

    @KafkaListener(topics = NewsKafkaProducer.TOPIC, groupId = "crypto-analyst-group")
    public void consume(NewsEvent event) {
        log.info("Received Kafka event id={} symbol={}", event.id(), event.symbol());
        NewsArticle article = new NewsArticle(
                event.id(), event.title(), event.content(),
                event.source(), event.symbol(), event.publishedAt());
        ingestNewsUseCase.ingest(article);
    }
}
