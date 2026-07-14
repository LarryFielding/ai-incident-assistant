from datetime import datetime
from uuid import UUID

from pydantic import BaseModel


class IncidentAnalysisRequestedEvent(BaseModel):
    event_id: UUID
    event_type: str
    occurred_at: datetime
    incident_id: int
    title: str
    description: str
    raw_logs: str | None = None
    service_name: str
    environment: str
    environment_name: str | None = None
    incident_occurred_at: datetime | None = None

class IncidentAnalyzedEvent(BaseModel):
    event_id: UUID
    event_type: str
    occurred_at: datetime
    incident_id: int

    summary: str
    severity: str
    category: str
    possible_root_cause: str | None = None
    suggested_actions: list[str]
    postmortem_draft: str | None = None