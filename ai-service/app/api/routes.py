from fastapi import APIRouter

from app.models.requests import IncidentAnalysisRequest
from app.models.responses import IncidentAnalysisResponse
from app.services.incident_analysis_service import analyze_incident

router = APIRouter()


@router.get("/health")
def health_check():
    return {
        "status": "ok",
        "service": "ai-service"
    }


@router.post("/analyze-incident", response_model=IncidentAnalysisResponse)
def analyze_incident_endpoint(request: IncidentAnalysisRequest):
    return analyze_incident(request)
