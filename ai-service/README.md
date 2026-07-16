# AI Service

Python/FastAPI microservice responsible for analyzing software incidents.

This service receives incident information, classifies the incident, suggests remediation actions, and generates a postmortem draft.

For now, the service supports:

- Rule-based incident analysis
- Optional LLM-based analysis using OpenAI
- Rule-based fallback when the LLM is disabled or fails
- 
## Tech Stack

- Python
- FastAPI

## API

## Current Endpoint

```text
POST /analyze-incident
```

## Health Check

```text
GET /health
```

### Analyze Incident

- **Endpoint:** `POST /analyze-incident`
- **Purpose:** Receives incident details, constructs a prompt for an LLM, and returns a structured JSON analysis.

## Virtual Environment

This project uses a Python virtual environment to manage dependencies.

### Activating the Virtual Environment

- **Windows:**
  ```shell
  .\.venv\Scripts\activate
  ```

- **macOS / Linux:**
  ```shell
  source .venv/bin/activate
  ```

### Deactivating the Virtual Environment

Once you are finished, you can deactivate the virtual environment with a single command:

```shell
deactivate
```

## Development

To run the service in a development environment, use the following command:

```shell
fastapi dev app/main.py
```

## Testing with Swagger UI

Once the server is running, you can access the interactive API documentation at [http://127.0.0.1:8000/docs](http://127.0.0.1:8000/docs).

To test the `POST /analyze-incident` endpoint, use the following JSON payloads:

# Case 1 - Database
```json
{
  "title": "Database timeout in payment service",
  "description": "Users report intermittent failures during checkout and payments are not being completed correctly.",
  "raw_logs": "java.sql.SQLTimeoutException: query timed out while waiting for connection pool",
  "service_name": "payment-service",
  "environment": "PROD",
  "environment_name": "payments-prod-us-east-1",
  "incident_occurred_at": "2026-05-28T03:15:00Z"
}
```
# Case 2 - Security
```json
{
  "title": "Unauthorized access errors in account service",
  "description": "Users are receiving authentication failures when accessing account details.",
  "raw_logs": "JWT token validation failed. unauthorized request. forbidden access.",
  "service_name": "account-service",
  "environment": "PROD",
  "environment_name": "accounts-prod-us-east-1",
  "incident_occurred_at": "2026-05-28T04:20:00Z"
}
```

# Case 3 - Network
```json
{
  "title": "Connection refused between services",
  "description": "Order service cannot communicate with inventory service.",
  "raw_logs": "connection refused while calling inventory-service. possible network issue.",
  "service_name": "order-service",
  "environment": "STAGING",
  "environment_name": "orders-staging",
  "incident_occurred_at": "2026-05-28T05:10:00Z"
}
```

# Case 4 - Critical production outage
```json
{
  "title": "Payment service is down",
  "description": "The payment service is unavailable and users cannot complete checkout.",
  "raw_logs": "service down. outage detected. payment-service unavailable.",
  "service_name": "payment-service",
  "environment": "PROD",
  "environment_name": "payments-prod-us-east-1",
  "incident_occurred_at": "2026-05-28T06:30:00Z"
}
```

## Response Example

```json
{
  "summary": "The incident affecting payment-service was classified as DATABASE with HIGH severity.",
  "severity": "HIGH",
  "category": "DATABASE",
  "possible_root_cause": "The incident may be related to database connectivity, slow queries, or resource contention.",
  "suggested_actions": [
    "Review database connection pool metrics.",
    "Check slow queries and database timeout logs.",
    "Validate database availability and recent schema changes."
  ],
  "postmortem_draft": "Postmortem draft for incident in payment-service. The incident occurred in PROD and was classified as DATABASE with HIGH severity. Initial analysis suggests that the issue may be related to: The incident may be related to database connectivity, slow queries, or resource contention. Further investigation is required to confirm the root cause, assess impact, and define preventive actions."
}
```

## Request Contract

| Field | Type | Required | Notes |
|---|---|---:|---|
| `title` | string | yes | Incident title |
| `description` | string | yes | Incident description |
| `raw_logs` | string or null | no | Raw logs or error messages |
| `service_name` | string | yes | Affected service |
| `environment` | enum | yes | `LOCAL`, `DEV`, `QA`, `STAGING`, `PROD` |
| `environment_name` | string or null | no | Deployment, cluster, region, or environment alias |
| `incident_occurred_at` | datetime | yes | ISO-8601 datetime |

## Response Contract

| Field | Type | Notes |
|---|---|---|
| `summary` | string | Short incident summary |
| `severity` | enum | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `category` | enum | `DATABASE`, `NETWORK`, `APPLICATION`, `INFRASTRUCTURE`, `SECURITY`, `PERFORMANCE`, `UNKNOWN` |
| `possible_root_cause` | string | Initial root cause hypothesis |
| `suggested_actions` | array of strings | Recommended next steps |
| `postmortem_draft` | string | Initial postmortem draft |

## Environment Variables

Create a local `.env` file in the `ai-service` folder.

```env
USE_LLM=false
LLM_MODEL=gpt-4o-mini
OPENAI_API_KEY=
```

For local rule-based analysis:

```env
USE_LLM=false
```

For LLM-based analysis:

```env
USE_LLM=true
LLM_MODEL=gpt-4o-mini
OPENAI_API_KEY=your_api_key_here
```

Important:

- Do not commit `.env`.
- Do not hardcode API keys.
- If the LLM fails, the service falls back to rule-based analysis.

## Running Locally

Activate the virtual environment.

PowerShell:

```powershell
.\.venv\Scripts\Activate.ps1
```

Install dependencies:

```powershell
pip install -r requirements.txt
```

Run the service:

```powershell
uvicorn app.main:app --reload
```

Swagger UI:

```text
http://127.0.0.1:8000/docs
```

## Future Java Integration Notes

This service is expected to be consumed later by the Spring Boot `incident-service`.

Current stable endpoint:

```text
POST /analyze-incident
```

Important JSON convention:

- This service uses `snake_case` JSON fields.
- Java/Spring Boot usually uses `camelCase` fields.
- In Phase 3, the Java DTOs should map correctly to this contract.

Example mapping:

| Python/FastAPI field | Java DTO field |
|---|---|
| `raw_logs` | `rawLogs` |
| `service_name` | `serviceName` |
| `environment_name` | `environmentName` |
| `incident_occurred_at` | `incidentOccurredAt` |
| `possible_root_cause` | `possibleRootCause` |
| `suggested_actions` | `suggestedActions` |
| `postmortem_draft` | `postmortemDraft` |

Phase 3 should not change this contract unless there is a clear reason.

# Running `ai-service`: FastAPI and Kafka Consumer

## Objective

The `ai-service` can currently receive incident analysis requests through two different paths:

1. Through the FastAPI HTTP endpoint.
2. Through events consumed from Kafka.

Both paths reuse the same internal analysis logic.

---

## 1. Activate the virtual environment

From PowerShell:

```powershell
cd "C:\Users\Larry Camarena\Documents\proyectos\ai-incident-assistant\ai-service"
```

Then run:

```powershell
.\.venv\Scripts\Activate.ps1
```

---

## 2. Run the Kafka consumer

To start the process that listens for analysis requests from Kafka:

```powershell
python -m app.messaging.kafka_consumer
```

The terminal must remain open while the consumer is running.

This process listens to the following topic:

```text
incident.analysis.requested
```

When it receives an event, it:

1. Deserializes the JSON.
2. Validates it with Pydantic.
3. Transforms it into an `IncidentAnalysisRequest`.
4. Runs the internal analysis logic.
5. Builds an `IncidentAnalyzedEvent`.
6. Publishes the result to:

```text
incident.analyzed
```

---

## 3. Current Kafka flow

The event-driven flow is:

```text
Postman
   ↓ HTTP
Spring Boot: POST /api/incidents/{id}/analysis-requests
   ↓ publishes event
Kafka: incident.analysis.requested
   ↓ consumed by
Python kafka_consumer
   ↓ calls directly
Internal analysis logic
   ↓ publishes event
Kafka: incident.analyzed
   ↓ consumed by
Spring Boot listener
```

The FastAPI HTTP endpoint does not participate in this flow.

The Kafka consumer does not make an HTTP request to:

```text
http://localhost:8000/analyze-incident
```

Instead, it invokes the internal analysis function directly, for example:

```python
analysis_result = analyze_incident(analysis_request)
```

---

## 4. Run FastAPI

To start the traditional HTTP endpoint:

```powershell
uvicorn app.main:app --reload
```

This enables:

```http
POST /analyze-incident
```

and:

```http
GET /health
```

Swagger is available at:

```text
http://127.0.0.1:8000/docs
```

---

## 5. Traditional HTTP flow

The previous synchronous flow is:

```text
Spring Boot
   ↓ HTTP
FastAPI: POST /analyze-incident
   ↓
Internal analysis logic
   ↓ HTTP response
Spring Boot
```

This flow requires Uvicorn to be running.

The corresponding Java endpoint is:

```http
POST /api/incidents/{id}/analysis
```

This endpoint waits for the complete response from `ai-service` before returning a response to the client.

---

## 6. FastAPI and Kafka reuse the same logic

There are currently two different entry points into the analysis logic:

```text
FastAPI endpoint ──────┐
                       ├──→ analyze_incident(...)
Kafka consumer ────────┘
```

This avoids duplicating business logic.

The FastAPI endpoint handles HTTP transport.

The Kafka consumer handles event-driven transport.

The analysis logic remains separate from both entry mechanisms.

---

## 7. When to run each process

### HTTP flow only

Run:

```powershell
uvicorn app.main:app --reload
```

Use it with:

```http
POST /api/incidents/{id}/analysis
```

### Kafka flow only

Run:

```powershell
python -m app.messaging.kafka_consumer
```

Use it with:

```http
POST /api/incidents/{id}/analysis-requests
```

### Both flows available

Open two terminals inside `ai-service`.

Terminal 1:

```powershell
uvicorn app.main:app --reload
```

Terminal 2:

```powershell
python -m app.messaging.kafka_consumer
```

This keeps both the HTTP flow and the Kafka flow available.

---

## 8. Current project state

Both paths are currently preserved:

```text
POST /api/incidents/{id}/analysis
→ synchronous HTTP integration
→ requires FastAPI/Uvicorn

POST /api/incidents/{id}/analysis-requests
→ asynchronous Kafka integration
→ requires kafka_consumer.py
```

The HTTP flow is temporarily retained as a baseline and fallback while the Kafka integration is completed and validated.