package com.winter107r.cryptoanalyst.infrastructure.messaging.kafka;

import java.time.Instant;

/** Kafka message payload — serialised as JSON. */
public record NewsEvent(
        String id,
        String title,
        String content,
        String source,
        String symbol,
        Instant publishedAt
) {}
