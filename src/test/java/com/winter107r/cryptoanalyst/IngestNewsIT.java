package com.winter107r.cryptoanalyst;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winter107r.cryptoanalyst.adapter.web.dto.IngestRequest;
import com.winter107r.cryptoanalyst.adapter.web.dto.QueryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test: real MySQL + Redis + Kafka via Testcontainers.
 * Covers the full ingest → query flow end-to-end.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@SuppressWarnings({"resource", "null"})
class IngestNewsIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.3"))
            .withDatabaseName("crypto_analyst")
            .withUsername("analyst")
            .withPassword("analyst123");

    @Container
    @ServiceConnection
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void ingestEndpointReturns202() throws Exception {
        IngestRequest req = new IngestRequest(
                "BTC breaks 100k",
                "Bitcoin surged past the $100,000 mark.",
                "CoinDesk",
                "BTC");

        mockMvc.perform(post("/api/news/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted());
    }

    @Test
    void queryEndpointReturnsInsight() throws Exception {
        QueryRequest req = new QueryRequest("What are the risks for BTC?", 3);

        mockMvc.perform(post("/api/insights/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").isString())
                .andExpect(jsonPath("$.cached").isBoolean());
    }
}
