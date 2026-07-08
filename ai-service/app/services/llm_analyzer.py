from openai import OpenAI

from app.core.config import settings
from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse


def analyze_with_llm(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    if not settings.openai_api_key:
        raise ValueError("OPENAI_API_KEY is not configured")

    client = OpenAI(api_key=settings.openai_api_key)

    completion = client.chat.completions.parse(
        model=settings.llm_model,
        messages=[
            {
                "role": "system",
                "content": (
                    "You are an AI incident analysis assistant. "
                    "Analyze software production incidents and return only structured data "
                    "matching the required response schema."
                ),
            },
            {
                "role": "user",
                "content": build_incident_prompt(request),
            },
        ],
        response_format=IncidentAnalysisResponse,
    )

    parsed_response = completion.choices[0].message.parsed

    if parsed_response is None:
        raise ValueError("LLM response could not be parsed")

    return parsed_response


def build_incident_prompt(request: IncidentAnalysisRequest) -> str:
    return f"""
Analyze the following software incident.

Title:
{request.title}

Description:
{request.description}

Raw logs:
{request.raw_logs or "No raw logs provided"}

Service name:
{request.service_name}

Environment:
{request.environment.value}

Environment name:
{request.environment_name or "Not provided"}

Incident occurred at:
{request.incident_occurred_at}

Return an incident analysis with:
- summary
- severity
- category
- possible_root_cause
- suggested_actions
- postmortem_draft

Use the available enum values only.
"""