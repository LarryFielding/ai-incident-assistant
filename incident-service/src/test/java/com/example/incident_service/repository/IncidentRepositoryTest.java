package com.example.incident_service.repository;

import com.example.incident_service.entity.Incident;
import com.example.incident_service.entity.IncidentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
// Evita que Spring intente reemplazar PostgreSQL con una base de datos en memoria (H2)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IncidentRepositoryTest {
    // Define el contenedor efímero de PostgreSQL para la prueba
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private IncidentRepository incidentRepository;

    @Test
    @DisplayName("Debería guardar un incidente exitosamente y aplicar @PrePersist")
    void shouldSaveIncidentSuccessfully() {
        // Given (Dado un incidente preparado)
        Incident incident = Incident.builder()
                .title("Database connection timeout")
                .description("Connection pool exhausted in production")
                .serviceName("payment-service")
                .environment("PROD")
                .build();

        // When (Cuando se guarda en el repositorio)
        Incident savedIncident = incidentRepository.save(incident);

        // Then (Entonces se verifican las aserciones)
        assertThat(savedIncident.getId()).isNotNull();
        assertThat(savedIncident.getStatus()).isEqualTo(IncidentStatus.OPEN); // Probando el @PrePersist
        assertThat(savedIncident.getCreatedAt()).isNotNull();
        assertThat(savedIncident.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debería buscar un incidente por ID")
    void shouldFindIncidentById() {
        // Given
        Incident incident = Incident.builder()
                .title("Memory Leak")
                .description("JVM heap usage at 98%")
                .serviceName("auth-service")
                .environment("STAGE")
                .build();
        Incident saved = incidentRepository.save(incident);

        // When
        Optional<Incident> found = incidentRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Memory Leak");
    }

}
