from datetime import datetime

from pydantic import BaseModel, Field

from app.models.enums import Environment


class IncidentAnalysisRequest(BaseModel):
    title: str = Field(min_length=5, max_length=150)
    description: str = Field(min_length=20, max_length=4000)
    raw_logs: str | None = Field(default=None, max_length=20000)
    service_name: str = Field(min_length=2, max_length=100)
    environment: Environment
    environment_name: str | None = Field(default=None, max_length=150)
    incident_occurred_at: datetime