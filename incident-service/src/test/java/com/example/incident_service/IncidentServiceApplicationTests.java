package com.example.incident_service;

import com.example.incident_service.dto.CreateIncidentRequest;
import com.example.incident_service.dto.IncidentResponse;
import com.example.incident_service.entity.Incident;
import com.example.incident_service.entity.IncidentStatus;
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

import java.net.URI;

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

		ResponseEntity<Void> response =
				restTemplate.exchange(
						"/api/incidents",
						HttpMethod.POST,
						httpRequest,
						Void.class
				);

		assertThat(response.getStatusCode())
				.isEqualTo(HttpStatus.CREATED);

		URI locationOfNewIncident = response.getHeaders().getLocation();

		ResponseEntity<String> getResponse = restTemplate
				.getForEntity(locationOfNewIncident, String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldReturnAnIncidentWhenDataIsSaved() {
		Incident incident = new Incident();
		incident.setTitle("Existing Incident");
		incident.setDescription("Description for existing incident");
		incident.setStatus(IncidentStatus.OPEN);
		incident.setServiceName("incident-service");
		incident.setEnvironment("PROD");
		Incident savedIncident = incidentRepository.save(incident);
		ResponseEntity<IncidentResponse> response =
				restTemplate.getForEntity("/api/incidents/{id}", IncidentResponse.class, savedIncident.getId());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().id()).isEqualTo(savedIncident.getId());
		assertThat(response.getBody().title()).isEqualTo("Existing Incident");

	}

	@Test
	void shouldNotReturnAnIncidentWithUnknowId() {
		Long unknownId = 12345L;
		ResponseEntity<String> response =
				restTemplate.getForEntity("/api/incidents/{id}", String.class, unknownId);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	void shouldReturnAllIncidentsWhenListIsRequested(){
		//TODO
	}

	void shouldNotCreateAnIncidentWithAnId() {
		//TODO
	}

	void shouldUpdateStatusOfAnExistingIncident() {
		//TODO
	}
}
