from openai import OpenAI
import google.generativeai as genai
import json

from app.core.config import settings
from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse
from app.models.enums import Severity, IncidentCategory


def analyze_with_llm(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    if settings.llm_provider == "openai":
        return analyze_with_openai(request)
    elif settings.llm_provider == "google":
        return analyze_with_google(request)
    else:
        raise ValueError(f"Unsupported LLM provider: {settings.llm_provider}")


def analyze_with_openai(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
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


def analyze_with_google(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    if not settings.google_api_key:
        raise ValueError("GOOGLE_API_KEY is not configured")

    genai.configure(api_key=settings.google_api_key)

    model = genai.GenerativeModel(
        settings.google_llm_model,
        generation_config=genai.GenerationConfig(
            response_mime_type="application/json"
        )
    )

    prompt = build_incident_prompt(request)
    response = model.generate_content(prompt)

    # The response from the Google API needs to be parsed into the Pydantic model.
    # Assuming the response text is a JSON string that matches the IncidentAnalysisResponse schema.
    return IncidentAnalysisResponse.parse_raw(response.text)


def build_incident_prompt(request: IncidentAnalysisRequest) -> str:
    severity_values = ", ".join([f"'{s.value}'" for s in Severity])
    category_values = ", ".join([f"'{c.value}'" for c in IncidentCategory])

    return f"""
Analyze the following software incident and return a valid JSON object.

**Incident Details:**
- **Title:** {request.title}
- **Description:** {request.description}
- **Raw Logs:** {request.raw_logs or "No raw logs provided"}
- **Service Name:** {request.service_name}
- **Environment:** {request.environment.value}
- **Environment Name:** {request.environment_name or "Not provided"}
- **Incident Occurred At:** {request.incident_occurred_at}

**Instructions:**
Return a JSON object with the following fields:
- "summary": A concise summary of the incident.
- "severity": The severity of the incident. Must be one of: {severity_values}.
- "category": The category of the incident. Must be one of: {category_values}.
- "possible_root_cause": A single string describing the most likely root cause.
- "suggested_actions": A list of strings with recommended actions to resolve the incident.
- "postmortem_draft": A single string containing a draft for a postmortem report.

Ensure the output is a single, valid JSON object and nothing else.
"""
