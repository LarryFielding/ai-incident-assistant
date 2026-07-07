from fastapi import FastAPI

from app.models.enums import IncidentCategory, Severity
from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse

app = FastAPI(
    title="AI Incident Assistant - AI Service",
    version="0.1.0"
)


@app.get("/health")
def health_check():
    return {
        "status": "ok",
        "service": "ai-service"
    }


@app.post("/analyze-incident", response_model=IncidentAnalysisResponse)
def analyze_incident(request: IncidentAnalysisRequest):
    return IncidentAnalysisResponse(
        summary=f"Incident reported for service {request.service_name} in {request.environment}.",
        severity=Severity.HIGH,
        category=IncidentCategory.UNKNOWN,
        possible_root_cause="The root cause has not been determined yet. This is a mock analysis.",
        suggested_actions=[
            "Review application logs around the incident timestamp.",
            "Check recent deployments or configuration changes.",
            "Validate service health and infrastructure metrics."
        ],
        postmortem_draft=(
            f"An incident was reported for service {request.service_name}. "
            "The system generated an initial mock analysis. "
            "Further investigation is required to confirm the actual root cause and remediation steps."
        )
    )