# AI Incident Assistant - Project Context

## Project Goal

The goal of this project is to build a realistic backend-oriented AI-powered incident management assistant.

The system is designed to help engineering or operations teams:

- register production incidents,
- attach descriptions and logs,
- analyze incidents using AI,
- classify severity and categories,
- suggest remediation steps,
- generate postmortem drafts,
- persist incident history.

The project is intentionally designed as a portfolio-quality backend application focused on:

- Java/Spring Boot backend development,
- API design,
- microservice communication,
- Dockerization,
- cloud deployment,
- practical AI integration.

---

# Architecture Overview

## High-Level Architecture

```text
[Client / Postman / Future Frontend]
                |
                v
      [Spring Boot Incident Service]
          - Incident CRUD
          - Business logic
          - Persistence
          - Publishes/Subscribes to Kafka
                |
                v (Events)
           [Apache Kafka]
                |
                v (Events)
         [Python AI Service]
          - Prompt handling
          - LLM integration
          - Structured AI responses
                |
                v
             [LLM API]
```

---

# Tech Stack

## Core Backend

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven
- Flyway (for migrations)
- Testcontainers (for integration testing)

## AI Service

- Python
- FastAPI
- LangChain / Instructor (planned)
- Pydantic v2 (planned)

## Infrastructure

- Docker
- Docker Compose

## Messaging / Event Streaming (planned)

- Apache Kafka
- Spring for Apache Kafka

## Planned Cloud Deployment

Preferred options discussed:

- GCP (recommended for MVP simplicity)
- AWS (enterprise-oriented option)
- OCI (lowest-cost option)

Current preferred option:

- GCP

---

# Repository Structure

```text
ai-incident-assistant/
│
├── incident-service/     # Spring Boot backend
│
├── ai-service/           # Python AI service (future)
│
├── infra/                # Infrastructure files (future)
│
├── README.md
├── .gitignore
└── .gitattributes
```

---

# Current Git Setup

The repository was initialized manually to practice Git/GitHub workflow.

Important notes:

- Root repository uses a centralized `.gitignore`
- `.gitattributes` is versioned
- Maven Wrapper files ARE committed:
  - `mvnw`
  - `mvnw.cmd`
  - `.mvn/`

Main branch:

```text
main
```

---



# Package Structure

Base package:

```text
com.example.incidentservice
```

Recommended structure:

```text
com.example.incidentservice
  ├── controller
  ├── dto
  ├── entity
  ├── repository
  ├── service
  ├── exception
  └── config
```

---

# PHASE 1 - Spring Boot Backend Foundation

## Goal

Build a functional REST API capable of managing incidents with PostgreSQL persistence.

---

## Features Implemented / Planned

### Incident CRUD

Endpoints:

```text
POST   /api/incidents
GET    /api/incidents
GET    /api/incidents/{id}
PATCH  /api/incidents/{id}/status
```

---

## Entity Design

### Incident

Fields:

- id
- title
- description
- rawLogs
- serviceName
- environment
- status
- createdAt
- updatedAt

### IncidentStatus enum

Values:

```text
OPEN
INVESTIGATING
MITIGATED
RESOLVED
```

---

## Spring Components

### DTOs

- CreateIncidentRequest
- IncidentResponse
- UpdateIncidentStatusRequest

### Layers

- Controller
- Service
- Repository
- Exception handling

---

## Database

PostgreSQL via Docker Compose.

Initial development configuration:

- Flyway for managing database migrations (ddl-auto is set to validate)
- Local PostgreSQL container
- Testcontainers for robust data layer integration testing

---

## Notes Learned During Phase 1

### Maven Wrapper

Use:

```powershell
.\mvnw.cmd spring-boot:run
```

instead of:

```powershell
mvn spring-boot:run
```

### Tests

The default generated Spring Boot test may fail while the application is incomplete.

Temporary acceptable options:

- delete placeholder test,
- or skip tests during packaging.

---

# PHASE 2 - Python AI Service

## Goal

Create a lightweight AI microservice responsible for incident analysis.

---

## Planned Stack

- Python
- FastAPI

---

## Endpoint

```text
POST /analyze-incident
```

---

## Expected Input

```json
{
  "title": "Database timeout in payment service",
  "description": "Users report intermittent failures during checkout",
  "raw_logs": "java.sql.SQLTimeoutException: query timed out",
  "service_name": "payment-service",
  "environment": "PROD",
  "environment_name": "payments-prod-us-east-1",
  "incident_occurred_at": "2026-05-28T03:15:00Z"
}
```

---

## Expected Output

```json
{
  "summary": "The payment service experienced intermittent database timeouts during checkout.",
  "severity": "HIGH",
  "category": "DATABASE",
  "possible_root_cause": "The database connection pool may have been saturated or queries may have exceeded timeout thresholds.",
  "suggested_actions": [
    "Check database connection pool metrics.",
    "Review slow query logs during the incident window.",
    "Verify whether recent deployments changed database access patterns."
  ],
  "postmortem_draft": "During the incident window, users experienced intermittent checkout failures caused by database timeouts in the payment service..."
}
```

---

## AI Strategy

Important architectural decision:

- DO NOT train a custom model.
- Use an external LLM API.
- Python service handles:
  - prompt construction,
  - response validation using Pydantic v2 to strictly match expected JSON structures,
  - structured JSON output using libraries like LangChain or Instructor.

---

# PHASE 3 - Java ↔ Python Integration (Event-Driven)

## Goal

Allow Spring Boot to orchestrate AI analysis without synchronous blocking calls.

---

## Planned Features

### Flow (Event-Driven via Kafka)

1. Read incident from PostgreSQL
2. Publish `incident.created` event to Kafka
3. Python AI service consumes event and performs LLM analysis
4. Python AI service publishes `incident.analyzed` event back to Kafka
5. Java service consumes analysis result and updates database

---

## Planned Entity

### IncidentAnalysis

Fields:

- summary
- severity
- category
- possibleRootCause
- suggestedActions
- postmortemDraft
- modelName
- timestamps

---

# PHASE 4 - Dockerization

## Goal

Containerize the entire system.

---

## Planned Containers

- incident-service
- ai-service
- postgres
- kafka

---

## Planned Files

- Dockerfile (Spring Boot)
- Dockerfile (FastAPI)
- docker-compose.yml

---

# PHASE 5 - Kafka Event Streaming (Advanced)

## Goal

Introduce and reinforce asynchronous event-driven communication into the platform.

This phase exists both to improve architecture realism and to practice distributed systems concepts commonly used in enterprise backend development. Since the core flow is moved to Phase 3, this phase focuses on robustness.

---

## Planned Stack

- Apache Kafka
- Spring for Apache Kafka
- Kafka UI (optional)

---

## Planned Event Flow

### Example Flow

```text
Incident Created
        ↓
Kafka Topic: incident.created
        ↓
AI Analysis Consumer
        ↓
Incident Analysis Generated
        ↓
Kafka Topic: incident.analyzed
```

---

## Planned Use Cases

### Incident Events

Events to publish:

- incident.created
- incident.status.updated
- incident.analysis.requested
- incident.analysis.completed

---

## Learning Goals

This phase is intended to reinforce:

- event-driven architecture
- asynchronous communication
- producer/consumer patterns
- distributed systems concepts
- retry/error handling
- message serialization
- backend scalability patterns

---

## Potential Enhancements

- dead letter queues
- retries
- schema registry
- Avro or Protobuf serialization
- multiple consumers
- metrics/monitoring

---

# PHASE 6 - Hardening / Production Improvements

## Goal

Improve the project to look more production-ready.

---

## Planned Improvements

- validation improvements
- structured logging
- better exception handling
- Swagger / OpenAPI
- environment variables
- health checks
- cleaner DTO separation
- sorting/filtering
- centralized configuration
- request tracing (Distributed Tracing using OpenTelemetry or Spring Cloud Sleuth / Micrometer Tracing)
- metrics via spring-boot-starter-actuator, Prometheus, and Grafana

---

# PHASE 7 - Cloud Deployment

## Goal

Deploy the MVP publicly.

---

## Preferred Initial Target

GCP

---

## Potential Deployment Design

### GCP

- Cloud Run
- Cloud SQL

### AWS

- ECS/Fargate
- RDS

### OCI

- VM + Docker Compose

---

# Long-Term Future Improvements

Potential future ideas after MVP:

- Slack/Teams integration
- Vector database / RAG
- Authentication
- Real-time alert ingestion
- Observability dashboards
- Incident timelines
- Multi-tenant support
- AI remediation suggestions
- Metrics pipeline
- Grafana dashboards
- OpenTelemetry tracing

---

# Main Learning Objectives

This project is intended to strengthen skills in:

- Spring Boot
- REST API design
- layered architecture
- JPA/Hibernate
- PostgreSQL
- Docker
- Kafka
- service-to-service communication
- AI integration patterns
- cloud deployment
- Git/GitHub workflows
- distributed systems fundamentals

The project prioritizes understanding and architecture quality over rapid code generation.