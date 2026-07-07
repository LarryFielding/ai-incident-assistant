# AI Service

This service is a lightweight Python-based microservice responsible for analyzing incident data using an external Large Language Model (LLM).

## Tech Stack

- Python
- FastAPI

## API

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

To test the `POST /analyze-incident` endpoint, use the following JSON payload:

```json
{
  "title": "Database timeout in payment service",
  "description": "Users report intermittent failures during checkout and payments are not being completed correctly.",
  "raw_logs": "java.sql.SQLTimeoutException: query timed out",
  "service_name": "payment-service",
  "environment": "PROD",
  "environment_name": "payments-prod-us-east-1",
  "incident_occurred_at": "2026-05-28T03:15:00Z"
}
```
