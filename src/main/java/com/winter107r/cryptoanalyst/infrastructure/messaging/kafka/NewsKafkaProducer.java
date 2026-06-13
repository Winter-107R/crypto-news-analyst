package com.winter107r.cryptoanalyst.infrastructure.messaging.kafka;

import com.winter107r.cryptoanalyst.domain.model.NewsArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Publishes NewsEvent to Kafka topic — called by REST /news/ingest endpoint. */
@Component
public class NewsKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(NewsKafkaProducer.class);
    static final String TOPIC = "crypto.news.raw";

    private final KafkaTemplate<String, NewsEvent> kafkaTemplate;

    public NewsKafkaProducer(KafkaTemplate<String, NewsEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(NewsArticle article) {
        NewsEvent event = new NewsEvent(
                article.getId(), article.getTitle(), article.getContent(),
                article.getSource(), article.getSymbol(), article.getPublishedAt());

        String symbol = Objects.requireNonNull(article.getSymbol(), "article symbol must not be null");
        kafkaTemplate.send(TOPIC, symbol, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) log.error("Failed to publish article {}: {}", article.getId(), ex.getMessage());
                    else log.debug("Published article {} to partition {}", article.getId(),
                                   result.getRecordMetadata().partition());
                });
    }
}
