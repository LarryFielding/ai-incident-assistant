package com.example.incident_service;

import com.example.incident_service.dto.CreateIncidentRequest;
import com.example.incident_service.dto.IncidentResponse;
import com.example.incident_service.entity.Incident;
import com.example.incident_service.repository.IncidentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

// Full endpoint integration test
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
class IncidentServiceApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

	@Autowired
    TestRestTemplate restTemplate;

	@Autowired
	private IncidentRepository incidentRepository;

	@AfterEach
	void cleanDatabase() {
		incidentRepository.deleteAll();
	}

	@Test
	void shouldCreateIncident() {
		CreateIncidentRequest request = new CreateIncidentRequest(
				"Payment service failure",
				"Payment requests are returning HTTP 500",
				"critical error",
				"payment-service",
				"PROD"
		);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<CreateIncidentRequest> httpRequest =
				new HttpEntity<>(request, headers);

		ResponseEntity<IncidentResponse> response =
				restTemplate.exchange(
						"/api/incidents",
						HttpMethod.POST,
						httpRequest,
						IncidentResponse.class
				);

		assertThat(response.getStatusCode())
				.isEqualTo(HttpStatus.CREATED);

		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().id()).isNotNull();
		assertThat(response.getBody().title())
				.isEqualTo("Payment service failure");

		Long createdIncidentId = response.getBody().id();

		Incident savedIncident = incidentRepository
				.findById(createdIncidentId)
				.orElseThrow();

		assertThat(savedIncident.getTitle())
				.isEqualTo("Payment service failure");

		assertThat(savedIncident.getServiceName())
				.isEqualTo("payment-service");
	}

}
