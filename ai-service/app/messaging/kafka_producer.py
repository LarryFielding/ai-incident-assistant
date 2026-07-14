from confluent_kafka import Producer

from app.messaging.models import IncidentAnalyzedEvent


KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
INCIDENT_ANALYZED_TOPIC = "incident.analyzed"


def create_producer() -> Producer:
    return Producer(
        {
            "bootstrap.servers": KAFKA_BOOTSTRAP_SERVERS,
        }
    )


def publish_incident_analyzed(
    producer: Producer,
    event: IncidentAnalyzedEvent,
) -> None:
    producer.produce(
        topic=INCIDENT_ANALYZED_TOPIC,
        key=str(event.incident_id),
        value=event.model_dump_json(),
    )

    producer.flush()