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
