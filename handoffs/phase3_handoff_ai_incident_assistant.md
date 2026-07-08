# AI Incident Assistant — Phase 3 Handoff

## Purpose of this file

This document summarizes the completed work from Phase 2 and provides the context needed to start Phase 3 in a new ChatGPT conversation.

The project is a learning/portfolio backend project intended to help transition toward modern backend development using Java/Spring Boot, microservices, Kafka/event-driven communication, Docker, cloud, and AI integration.

The next chat should continue in small, clear iterations. Avoid generating too much code at once. Explain concepts carefully, especially Kafka/event-driven design and Spring Boot integration patterns.

---

# Current Project Name

AI Incident Assistant

Suggested new chat title:

```text
Fase 3 AI Incident — Java Python Integration
```

---

# High-Level Goal of the Project

Build a realistic backend-oriented AI-powered incident management assistant.

The system should allow users or services to:

- register incidents,
- attach descriptions and logs,
- classify severity and category,
- generate suggested remediation actions,
- generate a postmortem draft,
- persist incident and analysis history,
- eventually use event-driven integration between Java and Python.

---

# Current Architecture

```text
Client / Postman / Future Frontend
        ↓
Spring Boot incident-service
        ↓ future integration
Python FastAPI ai-service
        ↓
External LLM provider
```

Later target architecture:

```text
Client / Postman / Future Frontend
        ↓
Spring Boot incident-service
        ↓
Apache Kafka
        ↓
Python FastAPI ai-service
        ↓
External LLM provider
```

---

# Repository Structure

Expected repository structure:

```text
ai-incident-assistant/
├── incident-service/
├── ai-service/
└── README.md
```

Current focus has been `ai-service`.

---

# Phase 2 Status — COMPLETED

Phase 2 goal:

Create a lightweight Python/FastAPI AI microservice responsible for analyzing incidents.

Status:

```text
Fase 2 — AI Service ✅ CLOSED
```

---

# Phase 2 Completed Checklist

```text
0. Diseñar dominio AI ✅
1. Crear carpeta ai-service ✅
2. Preparar proyecto FastAPI mínimo ✅
3. Crear modelos Pydantic ✅
4. Crear endpoint POST /analyze-incident ✅
5. Probar request/response en Swagger ✅
6. Agregar lógica mock ✅
6.1 Probar escenarios manuales en Swagger ✅
6.2 Commit de avance ✅
6.3 Refactorizar rutas con APIRouter ✅
7. Agregar LLM real ✅
   7.1 Crear soporte de configuración ✅
   7.2 Decidir provider/librería ✅
      - OpenAI SDK directo ✅
      - Google Gemini como provider alternativo ✅
   7.3 Separar analyzers conceptualmente ✅
   7.4 Implementar LLM analyzer ✅
   7.5 Agregar fallback ✅
   7.6 Probar desde Swagger con LLM real ✅
      - OpenAI invocado pero sin cuota ✅
      - Fallback ante insufficient_quota ✅
      - Google invocado con respuesta exitosa ✅
      - Respuesta validada con Pydantic ✅
   7.7 Reemplazar print por logging ✅
8. Preparar integración futura con Java ✅
   8.1 Confirmar contrato JSON final ✅
   8.2 Agregar ejemplos request/response al README ✅
   8.3 Documentar ejecución local ✅
   8.4 Documentar variables de entorno ✅
   8.5 Confirmar endpoint estable ✅
   8.6 Commit final de Fase 2 ✅ recomendado
```

---

# AI Service Current Structure

Expected `ai-service` structure:

```text
ai-service/
├── app/
│   ├── __init__.py
│   ├── main.py
│   ├── api/
│   │   ├── __init__.py
│   │   └── routes.py
│   ├── core/
│   │   ├── __init__.py
│   │   └── config.py
│   ├── models/
│   │   ├── __init__.py
│   │   ├── enums.py
│   │   ├── requests.py
│   │   └── responses.py
│   └── services/
│       ├── __init__.py
│       ├── incident_analysis_service.py
│       ├── rule_based_analyzer.py
│       └── llm_analyzer.py
├── README.md
├── requirements.txt
└── .venv/
```

Important Python note:

`__init__.py` files were added to package directories to avoid import/module resolution issues such as:

```text
Import error: No module named 'app'
```

---

# AI Service Endpoint

Current stable endpoint:

```text
POST /analyze-incident
```

Health endpoint:

```text
GET /health
```

Swagger UI:

```text
http://127.0.0.1:8000/docs
```

Run locally:

```powershell
.\.venv\Scripts\Activate.ps1
uvicorn app.main:app --reload
```

---

# AI Service Request Contract

Request JSON:

```json
{
  "title": "Database timeout in payment service",
  "description": "Users report intermittent failures during checkout. Several transactions are failing during payment confirmation.",
  "raw_logs": "java.sql.SQLTimeoutException: query timed out while executing checkout confirmation query",
  "service_name": "payment-service",
  "environment": "PROD",
  "environment_name": "payments-prod-us-east-1",
  "incident_occurred_at": "2026-05-28T03:15:00Z"
}
```

Fields:

| Field | Type | Required | Notes |
|---|---|---:|---|
| `title` | string | yes | Incident title |
| `description` | string | yes | Incident description |
| `raw_logs` | string or null | no | Raw logs or error messages |
| `service_name` | string | yes | Affected service |
| `environment` | enum | yes | `LOCAL`, `DEV`, `QA`, `STAGING`, `PROD` |
| `environment_name` | string or null | no | Deployment, cluster, region, or environment alias |
| `incident_occurred_at` | datetime | yes | ISO-8601 datetime |

---

# AI Service Response Contract

Response JSON:

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
  "postmortem_draft": "Postmortem draft for incident in payment-service. The incident occurred in PROD and was classified as DATABASE with HIGH severity. Initial analysis suggests that the issue may be related to database connectivity, slow queries, or resource contention. Further investigation is required to confirm the root cause, assess impact, and define preventive actions."
}
```

Fields:

| Field | Type | Notes |
|---|---|---|
| `summary` | string | Short incident summary |
| `severity` | enum | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `category` | enum | `DATABASE`, `NETWORK`, `APPLICATION`, `INFRASTRUCTURE`, `SECURITY`, `PERFORMANCE`, `UNKNOWN` |
| `possible_root_cause` | string | Initial root cause hypothesis |
| `suggested_actions` | array of strings | Recommended next steps |
| `postmortem_draft` | string | Initial postmortem draft |

---

# Enums

Environment:

```text
LOCAL
DEV
QA
STAGING
PROD
```

Severity:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

IncidentCategory:

```text
DATABASE
NETWORK
APPLICATION
INFRASTRUCTURE
SECURITY
PERFORMANCE
UNKNOWN
```

---

# AI Service Internal Flow

Current flow:

```text
routes.py
   ↓
incident_analysis_service.py
   ↓
if USE_LLM=true:
    analyze_with_llm()
        ↓
        provider selected by LLM_PROVIDER
        ├── OpenAI
        └── Google Gemini
    if LLM fails:
        fallback to rule_based_analyzer.py
else:
    rule_based_analyzer.py
```

Important design decisions:

- `incident_analysis_service.py` acts as orchestrator.
- `rule_based_analyzer.py` contains deterministic fallback logic.
- `llm_analyzer.py` handles external LLM providers.
- OpenAI and Google can both be selected by config.
- If LLM fails, endpoint should not break.
- Fallback is intentional and part of the design.

---

# Environment Variables

Local `.env` file is used for development.

Important:

- Do not commit `.env`.
- `.env` must be included in `.gitignore`.
- Do not hardcode API keys.

Expected settings in `app/core/config.py`:

```python
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    use_llm: bool = False
    llm_provider: str = "openai"

    openai_api_key: str | None = None
    llm_model: str = "gpt-4o-mini"

    google_api_key: str | None = None
    google_llm_model: str = "gemini-1.5-flash"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )


settings = Settings()
```

Example `.env` for rule-based mode:

```env
USE_LLM=false
```

Example `.env` for OpenAI:

```env
USE_LLM=true
LLM_PROVIDER=openai
OPENAI_API_KEY=your_openai_api_key_here
LLM_MODEL=gpt-4o-mini
```

Example `.env` for Google Gemini:

```env
USE_LLM=true
LLM_PROVIDER=google
GOOGLE_API_KEY=your_google_api_key_here
GOOGLE_LLM_MODEL=gemini-1.5-flash
```

---

# LLM Provider Status

## OpenAI

Status:

```text
Connection invoked ✅
Response success not validated due to account quota ❌
Fallback validated ✅
```

Observed error:

```text
429 insufficient_quota
```

Conclusion:

The code correctly reads the API key, attempts to call OpenAI, receives a real OpenAI API error, and falls back to rule-based analysis.

No payment method will be added for now.

## Google Gemini

Status:

```text
Connection invoked ✅
Response success validated ✅
Pydantic validation successful ✅
```

Google was tested because it had a usable free tier.

A JSON parsing issue occurred initially because the model generated invalid JSON with control characters or multiline strings. The fix was:

- Use `IncidentAnalysisResponse.model_validate_json(response_text)` for Pydantic v2.
- Strip the response text before parsing.
- Strengthen the prompt to require valid JSON only.
- Ask the model not to include raw line breaks inside JSON string values.

---

# Important LLM Analyzer Notes

`llm_analyzer.py` now supports multiple providers.

Recommended structure:

```python
def analyze_with_llm(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    if settings.llm_provider == "openai":
        return analyze_with_openai(request)
    elif settings.llm_provider == "google":
        return analyze_with_google(request)
    else:
        raise ValueError(f"Unsupported LLM provider: {settings.llm_provider}")
```

For Google/Pydantic v2, prefer:

```python
response_text = response.text.strip()
return IncidentAnalysisResponse.model_validate_json(response_text)
```

Avoid deprecated Pydantic v1 style:

```python
IncidentAnalysisResponse.parse_raw(response.text)
```

---

# Logging Status

The old `print(...)` in fallback was replaced with Python `logging`.

Current expected pattern in `incident_analysis_service.py`:

```python
import logging

logger = logging.getLogger(__name__)


def analyze_incident(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    if settings.use_llm:
        try:
            logger.info("LLM analysis is enabled. Attempting LLM analysis.")
            return analyze_with_llm(request)
        except Exception:
            logger.exception("LLM analysis failed. Falling back to rule-based analysis.")

    logger.info("Using rule-based incident analysis.")
    return analyze_with_rules(request)
```

This is intentionally basic logging. More advanced logging/observability can be handled later in a hardening phase.

---

# Java Integration Preparation

The AI service contract is stable for Phase 3.

Important JSON convention:

- FastAPI/Python uses `snake_case`.
- Java DTOs usually use `camelCase`.
- In Phase 3, Java DTO mapping must account for this.

Mapping examples:

| Python/FastAPI field | Java DTO field |
|---|---|
| `raw_logs` | `rawLogs` |
| `service_name` | `serviceName` |
| `environment_name` | `environmentName` |
| `incident_occurred_at` | `incidentOccurredAt` |
| `possible_root_cause` | `possibleRootCause` |
| `suggested_actions` | `suggestedActions` |
| `postmortem_draft` | `postmortemDraft` |

Potential Java-side options:

1. Use Jackson annotations such as `@JsonProperty("raw_logs")`.
2. Configure Jackson naming strategy for snake_case.
3. Create dedicated AI service integration DTOs separate from database/entity DTOs.

Recommended for learning clarity:

Use dedicated DTOs and explicit `@JsonProperty` annotations at first.

---

# Recommended Final Commit for Phase 2

Before starting Phase 3, run:

```bash
git status
```

Confirm `.env` is not included.

Recommended commit:

```bash
git add .
git commit -m "Finalize AI service LLM integration"
```

If README changes are separate:

```bash
git add ai-service/README.md
git commit -m "Document AI service contract and local setup"
```

---

# Phase 3 — Recommended Scope

Phase 3 title:

```text
Java ↔ Python Integration
```

Original roadmap says Phase 3 is event-driven via Kafka:

```text
1. Read incident from PostgreSQL
2. Publish incident.created event to Kafka
3. Python AI service consumes event and performs LLM analysis
4. Python AI service publishes incident.analyzed event back to Kafka
5. Java service consumes analysis result and updates database
```

However, because this is a learning project, Phase 3 should be done in small steps.

Recommended Phase 3 subphases:

```text
3.0 Review current incident-service status
3.1 Define Java-side AI request/response DTOs
3.2 Decide integration path for first iteration
3.3 Option A: start with synchronous HTTP call from Java to Python
3.4 Option B: go directly to Kafka/event-driven flow
3.5 Persist IncidentAnalysis result in Java/PostgreSQL
3.6 Add integration tests
3.7 Prepare for Kafka robustness later
```

Important recommendation:

Even though the roadmap says Kafka/event-driven integration, it may be educationally useful to first do a simple synchronous HTTP integration, then refactor to Kafka. This would teach:

- DTO mapping,
- service-to-service communication,
- error handling,
- timeouts,
- fallback behavior,
- then async messaging.

But if the user wants to follow the roadmap strictly, start Kafka directly.

---

# Phase 3 First Decision Needed

At the start of the next chat, ask/decide:

```text
Should Phase 3 start with a simple HTTP integration first, or go directly to Kafka?
```

Suggested answer for learning:

```text
Start with synchronous HTTP integration first, then move to Kafka.
```

Why:

- Faster to validate Java ↔ Python contract.
- Easier to debug.
- Lets the developer understand DTO mapping before adding Kafka complexity.
- Kafka can still be introduced afterward as the target architecture.

---

# Recommended Immediate Next Step in Phase 3

Start with:

```text
3.0 Review current incident-service status
```

Need to inspect/confirm:

- Current Spring Boot package structure.
- Current Incident entity fields.
- Current DTOs.
- Current endpoints.
- Current database migration/Flyway state.
- Current tests.
- Whether incident-service is running successfully.

Then continue with:

```text
3.1 Create Java DTOs matching ai-service contract
```

Potential DTOs:

```text
AiIncidentAnalysisRequest
AiIncidentAnalysisResponse
```

Fields should map to the Python contract.

---

# Suggested Phase 3 Checklist Template

Use this checklist in the next chat:

```text
Fase 3 — Java ↔ Python Integration

0. Revisar estado actual de incident-service ⬅️ siguiente
1. Definir estrategia inicial de integración: HTTP primero vs Kafka directo ⬜
2. Crear DTOs Java para contrato del ai-service ⬜
3. Crear cliente Java para llamar ai-service ⬜
4. Agregar configuración de URL del ai-service ⬜
5. Crear endpoint/flujo para solicitar análisis desde Java ⬜
6. Persistir resultado en IncidentAnalysis ⬜
7. Probar integración manual end-to-end ⬜
8. Agregar pruebas de integración ⬜
9. Preparar transición a Kafka/event-driven ⬜
```

If choosing Kafka directly, replace steps 3–5 with:

```text
3. Crear eventos incident.created / incident.analysis.requested ⬜
4. Configurar Kafka producer en incident-service ⬜
5. Configurar Kafka consumer en ai-service ⬜
6. Publicar incident.analyzed desde ai-service ⬜
7. Consumir incident.analyzed en incident-service ⬜
```

---

# Working Style for the Next Chat

The user prefers:

- Spanish explanations.
- Small iterations.
- Clear conceptual explanations.
- Relating Python/FastAPI concepts to Java/Spring Boot when helpful.
- Avoiding big code dumps.
- Keeping an updated checklist at the end of relevant responses.
- Treating this as a learning project, not just generating code.

The user has been committing after small stable changes. Continue recommending commits at stable checkpoints.

---

# Current Conclusion

Phase 2 is closed.

The Python AI service is ready enough for Java integration because:

- It has a stable endpoint.
- It has stable request/response contracts.
- It has rule-based analysis.
- It has LLM provider support.
- It has fallback behavior.
- It has logging.
- It has local configuration via `.env`.
- It has been tested manually in Swagger.

Next phase:

```text
Fase 3 — Java ↔ Python Integration
```
