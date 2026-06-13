package com.winter107r.cryptoanalyst.domain.port.in;

import com.winter107r.cryptoanalyst.domain.model.NewsArticle;

/** Inbound port — called by Kafka consumer and REST controller. */
public interface IngestNewsUseCase {
    void ingest(NewsArticle article);
}
