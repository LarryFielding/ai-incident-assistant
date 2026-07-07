from pydantic import BaseModel, Field

from app.models.enums import IncidentCategory, Severity


class IncidentAnalysisResponse(BaseModel):
    summary: str = Field(min_length=20, max_length=1000)
    severity: Severity
    category: IncidentCategory
    possible_root_cause: str = Field(min_length=10, max_length=1500)
    suggested_actions: list[str] = Field(min_length=1, max_length=10)
    postmortem_draft: str = Field(min_length=50, max_length=4000)