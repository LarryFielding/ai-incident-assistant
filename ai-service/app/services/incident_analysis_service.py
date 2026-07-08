from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse
from app.services.rule_based_analyzer import analyze_with_rules


def analyze_incident(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    return analyze_with_rules(request)