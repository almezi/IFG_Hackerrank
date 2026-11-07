# IFG_Hackerrank

Test automation project built with **Katalon Studio** for an IFG assignment. It validates an order-processing flow across two integration points: a **REST API** (create/get order) and an **Apache Kafka** event stream — end-to-end on localhost, backed by a mock order service.

## Tech Stack

| Component | Version / Tool |
|---|---|
| Test automation | Katalon Studio (Groovy keywords & scripts) |
| Messaging | Apache Kafka (Confluent Platform 7.6.0, `kafka-clients 3.7.0`) |
| Infra | Docker Compose (Zookeeper + Kafka + Kafka UI) |
| Mock service | Node.js (Express + kafkajs) |
| Build | Gradle (`com.katalon.gradle-plugin`) |

## Architecture

```
Katalon test ──POST /api/create──▶ order-service ──publish──▶ Kafka (ifg-topic)
     │                                  │
     │                                  └─ GET /api/get-order?id=...
     └──KafkaConsumer (kafka-clients)◀── ifg-topic
```

## Prerequisites

- [Katalon Studio](https://katalon.com/)
- [Docker](https://www.docker.com/) with Compose v2
- Node.js 14+ (for the mock order service)

## Getting Started

### 1. Start the Kafka stack

```bash
docker compose up -d
```

| Service | Address |
|---|---|
| Kafka broker | `localhost:9092` |
| Zookeeper | `localhost:2181` |
| Kafka UI | http://localhost:8085 |

Kafka UI is handy for eyeballing topics and messages while tests run.

### 2. Start the mock order service

```bash
cd order-service
npm install
npm start
```

The service listens on **http://localhost:8080**:

| Endpoint | Description |
|---|---|
| `POST /api/create` | Stores the order and publishes it to the `ifg-topic` Kafka topic |
| `GET /api/get-order?id=ORD-2026-001` | Returns a previously created order |
| `GET /health` | Liveness check |

### 3. Run the tests

Open `IFG_Hackerrank.prj` in Katalon Studio and run:

- **Test Suites → TS_Order_Flow_E2E** — the full flow (create → verify Kafka event → read back)
- or any single test case (see below)

## Test Cases

| Test case | What it does |
|---|---|
| `API/TC_CreateOrder_POST` | POSTs `Include/fixtures/order_data.json` to `/api/create`, asserts 200 and that the response echoes the order |
| `API/TC_GetOrder_GET` | GETs the order by id, asserts every field matches the fixture |
| `Test_Kafka_Listener` | E2E: creates the order via API, consumes the event from `ifg-topic`, asserts the event matches |
| `TC_Kafka_Test` | Kafka roundtrip: produces the fixture payload, consumes it back, asserts exact match |

Supporting keywords live in `Keywords/kafka/KafkaKeywords.groovy` (produce / consume / consume-until-match) and `Keywords/api/ApiKeywords.groovy` (createOrder / getOrder / fixture reader).

## Configuration

Global variables (Profiles → default profile):

| Variable | Default | Meaning |
|---|---|---|
| `BASE_URL` | `http://localhost:8080` | Order service base URL |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker |
| `ORDER_TOPIC` | `ifg-topic` | Topic for order events |

The test data contract (`Include/fixtures/order_data.json`):

```json
{
  "order_id": "ORD-2026-001",
  "customer_name": "Budi Santoso",
  "total_price": 150000,
  "status": "CREATED"
}
```

## Project Structure

```
├── docker-compose.yml          # Kafka + Zookeeper + Kafka UI (port 8085)
├── build.gradle                # kafka-clients dependency
├── IFG_Hackerrank.prj          # Katalon project file
├── order-service/              # Node.js mock order service (Express + kafkajs)
│   ├── server.js
│   └── package.json
├── Keywords/
│   ├── api/ApiKeywords.groovy  # REST keywords (createOrder / getOrder / fixture)
│   └── kafka/KafkaKeywords.groovy  # produce / consume / consume-until-match
├── Test Cases/
│   ├── API/
│   │   ├── TC_CreateOrder_POST.tc
│   │   └── TC_GetOrder_GET.tc
│   ├── TC_Kafka_Test.tc
│   └── Test_Kafka_Listener.tc  # E2E listener test
├── Test Suites/
│   └── TS_Order_Flow_E2E.ts    # POST → Kafka event → GET
├── Scripts/                    # Groovy scripts backing the test cases
├── Include/fixtures/
│   └── order_data.json         # Canonical order payload
├── Data Files/
│   └── order_data.json.dat     # Katalon data-file entity (JSON driver)
└── Library/                    # Vendored Kafka/SLF4J jars
```

## Notes

- The broker advertises `PLAINTEXT://localhost:9092`, so clients must connect from the host machine.
- The `ifg-topic` topic is auto-created on first publish.
- Reports land under `Reports/`; Katalon self-healing state is stored in `Reports/Self-healing/`.
- Running tests in **console mode** requires the separate Katalon Runtime Engine (KRE); from inside Katalon Studio it works out of the box.
