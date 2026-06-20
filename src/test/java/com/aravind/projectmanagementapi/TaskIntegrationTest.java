package com.aravind.projectmanagementapi;

import com.aravind.projectmanagementapi.dto.auth.AuthResponse;
import com.aravind.projectmanagementapi.dto.auth.RegisterResponse;
import com.aravind.projectmanagementapi.dto.project.ProjectResponse;
import com.aravind.projectmanagementapi.dto.task.TaskResponse;
import com.aravind.projectmanagementapi.respository.ProjectRepository;
import com.aravind.projectmanagementapi.respository.TaskRepository;
import com.aravind.projectmanagementapi.respository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskIntegrationTest extends BaseIntegrationTest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TaskRepository taskRepository;

    private String getToken(String email, String password) {
        restTemplate.postForEntity("/api/auth/register",
                Map.of("email", email, "password", password), RegisterResponse.class);
        return restTemplate.postForEntity("/api/auth/login",
                Map.of("email", email, "password", password), AuthResponse.class)
                .getBody().getAccessToken();
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private Long createProject(String token, String name) {
        return restTemplate.postForEntity("/api/projects",
                new HttpEntity<>(Map.of("name", name), authHeaders(token)),
                ProjectResponse.class).getBody().getId();
    }

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test @Order(1)
    @DisplayName("POST /api/projects/{id}/tasks - create task")
    void createTask() {
        String token = getToken("task1@test.com", "password123");
        Long pid = createProject(token, "TaskProject");
        ResponseEntity<TaskResponse> r = restTemplate.postForEntity(
                "/api/projects/" + pid + "/tasks",
                new HttpEntity<>(Map.of("title","Task1","status","TODO"), authHeaders(token)),
                TaskResponse.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(r.getBody().getTitle()).isEqualTo("Task1");
        assertThat(r.getBody().getProjectId()).isEqualTo(pid);
    }

    @Test @Order(2)
    @DisplayName("GET /api/projects/{id}/tasks - list tasks")
    void listTasks() {
        String token = getToken("task2@test.com", "password123");
        Long pid = createProject(token, "ListProject");
        restTemplate.postForEntity("/api/projects/" + pid + "/tasks",
                new HttpEntity<>(Map.of("title","T1","status","TODO"), authHeaders(token)),
                TaskResponse.class);
        restTemplate.postForEntity("/api/projects/" + pid + "/tasks",
                new HttpEntity<>(Map.of("title","T2","status","DONE"), authHeaders(token)),
                TaskResponse.class);
        ResponseEntity<List<TaskResponse>> r = restTemplate.exchange(
                "/api/projects/" + pid + "/tasks", HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)), new ParameterizedTypeReference<>(){});
        assertThat(r.getBody()).hasSize(2);
    }

    @Test @Order(3)
    @DisplayName("PUT /api/tasks/{id} - update task")
    void updateTask() {
        String token = getToken("task3@test.com", "password123");
        Long pid = createProject(token, "UpdateProject");
        Long tid = restTemplate.postForEntity("/api/projects/" + pid + "/tasks",
                new HttpEntity<>(Map.of("title","Old","status","TODO"), authHeaders(token)),
                TaskResponse.class).getBody().getId();
        ResponseEntity<TaskResponse> r = restTemplate.exchange("/api/tasks/" + tid,
                HttpMethod.PUT,
                new HttpEntity<>(Map.of("title","New","status","IN_PROGRESS"), authHeaders(token)),
                TaskResponse.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody().getTitle()).isEqualTo("New");
    }

    @Test @Order(4)
    @DisplayName("DELETE /api/tasks/{id} - delete task")
    void deleteTask() {
        String token = getToken("task4@test.com", "password123");
        Long pid = createProject(token, "DeleteProject");
        Long tid = restTemplate.postForEntity("/api/projects/" + pid + "/tasks",
                new HttpEntity<>(Map.of("title","ToDelete","status","TODO"), authHeaders(token)),
                TaskResponse.class).getBody().getId();
        ResponseEntity<Void> r = restTemplate.exchange("/api/tasks/" + tid,
                HttpMethod.DELETE, new HttpEntity<>(authHeaders(token)), Void.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test @Order(5)
    @DisplayName("GET /api/tasks/{id} - 403 for non-owner")
    void getTaskForbidden() {
        String t1 = getToken("taskown@test.com", "password123");
        String t2 = getToken("taskother@test.com", "password123");
        Long pid = createProject(t1, "OwnerProject");
        Long tid = restTemplate.postForEntity("/api/projects/" + pid + "/tasks",
                new HttpEntity<>(Map.of("title","Secret","status","TODO"), authHeaders(t1)),
                TaskResponse.class).getBody().getId();
        ResponseEntity<Map> r = restTemplate.exchange("/api/tasks/" + tid,
                HttpMethod.GET, new HttpEntity<>(authHeaders(t2)), Map.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
