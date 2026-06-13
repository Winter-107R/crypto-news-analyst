package com.winter107r.cryptoanalyst.infrastructure.config;

import com.winter107r.cryptoanalyst.application.usecase.IngestNewsService;
import com.winter107r.cryptoanalyst.application.usecase.QueryInsightService;
import com.winter107r.cryptoanalyst.domain.port.out.CachePort;
import com.winter107r.cryptoanalyst.domain.port.out.LlmPort;
import com.winter107r.cryptoanalyst.domain.port.out.NewsRepository;
import com.winter107r.cryptoanalyst.domain.port.out.VectorStorePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Composition root — wires application services with their port implementations.
 * Application services carry no Spring annotations; only this class touches the framework.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public IngestNewsService ingestNewsService(NewsRepository newsRepository,
                                               VectorStorePort vectorStorePort) {
        return new IngestNewsService(newsRepository, vectorStorePort);
    }

    @Bean
    public QueryInsightService queryInsightService(VectorStorePort vectorStorePort,
                                                   LlmPort llmPort,
                                                   CachePort cachePort) {
        return new QueryInsightService(vectorStorePort, llmPort, cachePort);
    }
}
