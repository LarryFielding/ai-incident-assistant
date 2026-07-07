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