# Crypto News Analyst

> RAG-powered crypto risk analyst built with Clean Architecture, Spring Boot 3, Kafka, Redis, and Spring AI.  
> Inspired by [RiskTagger (arXiv:2510.17848)](https://arxiv.org/abs/2510.17848) and [RAG for Crypto News (arXiv:2509.03527)](https://arxiv.org/abs/2509.03527).

---

## Why This Exists

Crypto markets move on news. LLMs hallucinate when asked about live events.  
This project solves both: news flows in via Kafka, is stored in MySQL + a keyword-vector index,
and every LLM answer is **grounded in retrieved articles** — not training-time knowledge.

---

## Architecture (Clean / Hexagonal)

```
┌─────────────────────────────────────────────────────────────────────┐
│  Inbound Adapters                                                   │
│  POST /api/news/ingest ──► NewsController                           │
│  POST /api/insights/query ──► InsightController                     │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ calls ports (interfaces only)
┌──────────────────────────────▼──────────────────────────────────────┐
│  Application Layer  (zero Spring annotations)                       │
│  IngestNewsService  ──► idempotent save → vector index              │
│  QueryInsightService──► cache → RAG retrieve → LLM → cache write    │
└────────────┬───────────────────────────────────────┬────────────────┘
             │ domain model only                     │
┌────────────▼──────────────────────┐   ┌───────────▼────────────────┐
│  Domain Layer                     │   │  Outbound Port Interfaces   │
│  NewsArticle · AnalysisQuery      │   │  NewsRepository             │
│  RiskInsight                      │   │  VectorStorePort            │
│  IngestNewsUseCase (port/in)      │   │  LlmPort                   │
│  QueryInsightUseCase (port/in)    │   │  CachePort                  │
└───────────────────────────────────┘   └───────────┬────────────────┘
                                                    │ implemented by
┌───────────────────────────────────────────────────▼────────────────┐
│  Infrastructure Layer                                               │
│  MySqlNewsAdapter      ── JPA + MySQL 8                            │
│  KeywordVectorAdapter  ── keyword search (no API key required)     │
│  OpenAiLlmAdapter      ── Spring AI ChatClient  (@Profile openai)  │
│  MockLlmAdapter        ── template response    (@Profile default)  │
│  RedisCacheAdapter     ── 5-min TTL JSON cache                     │
│  NewsKafkaProducer/Consumer ── async ingestion pipeline            │
└────────────────────────────────────────────────────────────────────┘
```

**Key design decisions:**
- Application services have **zero Spring annotations** — wired solely via `AppConfig` (composition root)
- `VectorStorePort` makes the vector backend swappable (ChromaDB, Pinecone) without touching domain code
- Dual LLM mode: runs fully **without any API key** via `MockLlmAdapter`; swap to real OpenAI with `--spring.profiles.active=openai`

---

## Research Grounding

| Paper | How it influenced this project |
|---|---|
| [RiskTagger arXiv:2510.17848](https://arxiv.org/abs/2510.17848) | Risk-aware retrieval: retrieve symbol-specific chunks before generation |
| [RAG for Crypto News arXiv:2509.03527](https://arxiv.org/abs/2509.03527) | Kafka → ingest → vector-index pipeline design |
| [Agentic RAG arXiv:2501.09136](https://arxiv.org/abs/2501.09136) | Cache-then-generate flow to reduce redundant LLM calls |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (records, sealed, text blocks) |
| Framework | Spring Boot 3.3.5 |
| Messaging | Apache Kafka (Confluent 7.6) |
| Cache | Redis 7.2 — 5-minute TTL |
| Database | MySQL 8.3 |
| AI / RAG | Spring AI 1.0 (OpenAI adapter, optional) |
| Build | Maven 3 |
| Containers | Docker Compose |

---

## Quick Start (no API key needed)

```bash
# 1. Start infrastructure
docker compose up -d

# 2. Build
mvn clean package -DskipTests

# 3. Run (mock LLM + keyword vector — no API key)
java -jar target/crypto-news-analyst-*.jar
```

The app starts on **http://localhost:8080**.

---

## API Examples

### Ingest a news article
```bash
curl -X POST http://localhost:8080/api/news/ingest \
  -H "Content-Type: application/json" \
  -d '{
    "id": "news-001",
    "title": "Bitcoin ETF inflows hit record $1.2B",
    "content": "Spot Bitcoin ETFs recorded record inflows as institutional demand surges.",
    "source": "CoinDesk",
    "symbol": "BTC"
  }'
# → 202 Accepted  (published to Kafka → consumed → stored in MySQL + vector index)
```

### Query for risk insights
```bash
curl -X POST http://localhost:8080/api/insights/query \
  -H "Content-Type: application/json" \
  -d '{"question": "What are the current risks for BTC?", "topK": 3}'
```

**Response:**
```json
{
  "question": "What are the current risks for BTC?",
  "answer": "Based on recent news: Bitcoin ETF inflows hit record levels...",
  "sourceTitles": ["Bitcoin ETF inflows hit record $1.2B"],
  "cached": false,
  "generatedAt": "2025-01-15T10:30:00Z"
}
```

**Re-query the same question** — served from Redis cache (`"cached": true`).

---

## Run With Real OpenAI

```bash
export OPENAI_API_KEY=sk-...
java -jar target/crypto-news-analyst-*.jar --spring.profiles.active=openai
```

---

## Project Structure

```
src/main/java/com/winter107r/cryptoanalyst/
├── domain/
│   ├── model/          # Pure Java value objects (no framework deps)
│   └── port/
│       ├── in/         # Use case interfaces (IngestNewsUseCase, QueryInsightUseCase)
│       └── out/        # Repository / infrastructure interfaces
├── application/
│   └── usecase/        # Business logic — zero Spring annotations
├── infrastructure/
│   ├── config/         # AppConfig — sole composition root
│   ├── llm/            # MockLlmAdapter + OpenAiLlmAdapter
│   ├── messaging/      # Kafka producer + consumer, Redis cache
│   ├── persistence/    # JPA entity + MySqlNewsAdapter + KeywordVectorAdapter
│   └── vector/         # (future: ChromaDB, Pinecone adapters)
└── adapter/
    └── web/            # REST controllers + DTOs
```

---

## Tests

```bash
mvn test
```

Unit tests cover:
- `IngestNewsServiceTest` — idempotency, vector-store delegation
- `QueryInsightServiceTest` — cache-hit path, RAG pipeline, empty-context handling, cache key stability

> ~60–65% line coverage. Integration tests (Testcontainers) are the natural next step.

---

## Infrastructure Ports

| Service | Default | This project |
|---|---|---|
| MySQL | 3306 | **3307** |
| Redis | 6379 | **6380** |
| Kafka | 9092 | **9093** |

Non-default ports prevent conflicts with existing local services.

---

## Author

Ratchawin Saithong · [github.com/Winter-107R](https://github.com/Winter-107R)

