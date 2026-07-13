package com.example.incident_service.messaging;

import com.example.incident_service.messaging.event.IncidentAnalysisRequestedEvent;
import com.example.incident_service.messaging.producer.IncidentAnalysisRequestedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

//@Component
@RequiredArgsConstructor
public class KafkaProducerSmokeTest {//implements CommandLineRunner {

    private final IncidentAnalysisRequestedProducer producer;

    //@Override
    public void run(String... args) throws Exception {
        IncidentAnalysisRequestedEvent event =
                new IncidentAnalysisRequestedEvent(
                        UUID.randomUUID(),
                        KafkaTopics.INCIDENT_ANALYSIS_REQUESTED,
                        OffsetDateTime.now(),
                        999L,
                        "Kafka smoke test",
                        "Temporary event used to verify Kafka publishing",
                        "java.sql.SQLTimeoutException: test timeout",
                        "incident-service",
                        "LOCAL",
                        "local-environment",
                        OffsetDateTime.now()
                );

        producer.publish(event);

        System.out.println(
                "Kafka smoke-test event sent: " + event.eventId()
        );
    }
}
