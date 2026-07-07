from app.models.enums import IncidentCategory, Severity
from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse


def analyze_incident(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    return IncidentAnalysisResponse(
        summary=f"Incident reported for service {request.service_name} in {request.environment.value}.",
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