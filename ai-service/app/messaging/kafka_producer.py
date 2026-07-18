from confluent_kafka import Producer

from app.core.config import settings
from app.messaging.models import IncidentAnalyzedEvent


def create_producer() -> Producer:
    return Producer(
        {
            "bootstrap.servers": settings.kafka_bootstrap_servers,
        }
    )


def publish_incident_analyzed(
    producer: Producer,
    event: IncidentAnalyzedEvent,
) -> None:
    producer.produce(
        topic=settings.incident_analyzed_topic,
        key=str(event.incident_id),
        value=event.model_dump_json(),
    )

    producer.flush()