package com.taskman.backend.controllers;

import com.taskman.backend.models.Task;
import com.taskman.backend.models.domains.ApiResponse;
import com.taskman.backend.models.dtos.CreateTaskDto;
import com.taskman.backend.models.enums.TaskStatus;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
public class TaskControllerE2ETest {
    @LocalServerPort
    private int port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    static {
        postgres.start();
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Autowired
    private TestRestTemplate restTemplate;

    private static String createdTaskId;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    public void ashouldCreateTask() {
        CreateTaskDto dto = new CreateTaskDto("Task 1", "Description 1", TaskStatus.TODO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateTaskDto> request = new HttpEntity<>(dto, headers);

        // Act
        ResponseEntity<ApiResponse<Task>> response = restTemplate.exchange(
                createURLWithPort("/api/v1/task"),
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<ApiResponse<Task>>() {}
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData().getTitle()).isEqualTo(dto.title());
        assertThat(response.getBody().getData().getDescription()).isEqualTo(dto.description());
        assertThat(response.getBody().getData().getStatus()).isEqualTo(dto.status());

        // Store the created task ID
        createdTaskId = response.getBody().getData().getId().toString();
    }

    @Test
    public void bshouldGetTaskById() {

        // Act
        ResponseEntity<ApiResponse<Task>> response = restTemplate.exchange(
                createURLWithPort("/api/v1/task/" + createdTaskId),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<Task>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getId().toString()).isEqualTo(createdTaskId);
    }

    @Test
    public void cshouldUpdateTask() {

        // Prepare update data
        CreateTaskDto updateDto = new CreateTaskDto("Updated Task", "Updated Description", TaskStatus.IN_PROGRESS);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateTaskDto> request = new HttpEntity<>(updateDto, headers);

        // Act
        ResponseEntity<ApiResponse<Task>> response = restTemplate.exchange(
                createURLWithPort("/api/v1/task/" + createdTaskId),
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<ApiResponse<Task>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getTitle()).isEqualTo(updateDto.title());
        assertThat(response.getBody().getData().getDescription()).isEqualTo(updateDto.description());
        assertThat(response.getBody().getData().getStatus()).isEqualTo(updateDto.status());
    }

    @Test
    public void dshouldGetTasks() {

        // Act
        ResponseEntity<ApiResponse<List<Task>>> response = restTemplate.exchange(
                createURLWithPort("/api/v1/task"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<List<Task>>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getData().toArray().length).isNotEqualTo(0);
    }

//    add codes to test get not existing id
    @Test
    public void eshouldReturn404WhenTaskNotFound() {
        // Arrange
        String taskId = "89965da0-c815-4542-9dbf-d899d8b19223";

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(createURLWithPort("/api/v1/task/" + taskId), ApiResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    //    add codes to test delete task
    @Test
    public void fshouldDeleteTask() {
        // Act
        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                createURLWithPort("/api/v1/task/" + createdTaskId),
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}