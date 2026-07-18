import json

from confluent_kafka import Consumer, KafkaError
from pydantic import ValidationError
from uuid import uuid4
from datetime import datetime, timezone

from app.core.config import settings
from app.messaging.models import IncidentAnalysisRequestedEvent
from app.messaging.kafka_producer import (
    create_producer,
    publish_incident_analyzed,
)
from app.messaging.models import (
    IncidentAnalysisRequestedEvent,
    IncidentAnalyzedEvent,
)
from app.models.requests import IncidentAnalysisRequest
from app.services.incident_analysis_service import analyze_incident


def process_analysis_request(
    event: IncidentAnalysisRequestedEvent,
) -> IncidentAnalyzedEvent:
    analysis_request = IncidentAnalysisRequest(
        title=event.title,
        description=event.description,
        raw_logs=event.raw_logs,
        service_name=event.service_name,
        environment=event.environment,
        environment_name=event.environment_name,
        incident_occurred_at=event.incident_occurred_at,
    )

    analysis_result = analyze_incident(analysis_request)

    return IncidentAnalyzedEvent(
        event_id=uuid4(),
        event_type="incident.analyzed",
        occurred_at=datetime.now(timezone.utc),
        incident_id=event.incident_id,
        summary=analysis_result.summary,
        severity=analysis_result.severity,
        category=analysis_result.category,
        possible_root_cause=analysis_result.possible_root_cause,
        suggested_actions=analysis_result.suggested_actions,
        postmortem_draft=analysis_result.postmortem_draft,
    )


def create_consumer() -> Consumer:
    return Consumer(
        {
            "bootstrap.servers": settings.kafka_bootstrap_servers,
            "group.id": settings.consumer_group_id,
            "auto.offset.reset": "earliest",
        }
    )


def consume_analysis_requests() -> None:
    consumer = create_consumer()
    producer = create_producer()

    consumer.subscribe([settings.analysis_requested_topic])

    print(
        f"Listening for Kafka events on "
        f"{settings.analysis_requested_topic}..."
    )

    try:
        while True:
            message = consumer.poll(1.0)

            if message is None:
                continue

            if message.error():
                if message.error().code() == KafkaError._PARTITION_EOF:
                    continue

                raise RuntimeError(message.error())

            raw_value = message.value().decode("utf-8")
            try:
                event_data = json.loads(raw_value)
                event = IncidentAnalysisRequestedEvent.model_validate(event_data)

                print("Received incident analysis request:")
                print(event.model_dump_json(indent=2))

                analyzed_event = process_analysis_request(event)
                print("Incident analysis completed:")

                publish_incident_analyzed(producer, analyzed_event)
                print("Incident analyzed event published:")

                print(analyzed_event.model_dump_json(indent=2))

            except (json.JSONDecodeError, ValidationError) as exc:
                print(f"Invalid Kafka event: {exc}")

    finally:
        consumer.close()


if __name__ == "__main__":
    consume_analysis_requests()