from app.core.config import settings
from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse
from app.services.llm_analyzer import analyze_with_llm
from app.services.rule_based_analyzer import analyze_with_rules


def analyze_incident(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    if settings.use_llm:
        try:
            return analyze_with_llm(request)
        except Exception as error:
            print(f"LLM analysis failed. Falling back to rule-based analysis. Error: {error}")

    return analyze_with_rules(request)