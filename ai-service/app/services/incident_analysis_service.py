import logging

from app.core.config import settings
from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse
from app.services.llm_analyzer import analyze_with_llm
from app.services.rule_based_analyzer import analyze_with_rules

logger = logging.getLogger(__name__)

def analyze_incident(request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
    if settings.use_llm:
        try:
            logger.info("LLM analysis is enabled. Attempting LLM analysis.")
            return analyze_with_llm(request)
        except Exception as error:
            logger.exception("LLM analysis failed. Falling back to rule-based analysis.")

    logger.info("Using rule-based incident analysis.")
    return analyze_with_rules(request)