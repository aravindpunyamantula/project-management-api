package com.aravind.projectmanagementapi;

import com.aravind.projectmanagementapi.dto.auth.AuthResponse;
import com.aravind.projectmanagementapi.dto.auth.RegisterResponse;
import com.aravind.projectmanagementapi.dto.project.ProjectResponse;
import com.aravind.projectmanagementapi.respository.ProjectRepository;
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
class ProjectIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

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

    @BeforeEach
    void setUp() { projectRepository.deleteAll(); userRepository.deleteAll(); }

    @Test @Order(1)
    @DisplayName("POST /api/projects - create project")
    void createProject() {
        String token = getToken("p1@test.com", "password123");
        ResponseEntity<ProjectResponse> r = restTemplate.postForEntity("/api/projects",
                new HttpEntity<>(Map.of("name","My Project","description","desc"), authHeaders(token)),
                ProjectResponse.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(r.getBody().getName()).isEqualTo("My Project");
        assertThat(r.getBody().getOwnerId()).isNotNull();
    }

    @Test @Order(2)
    @DisplayName("GET /api/projects - list only owned projects")
    void listProjects() {
        String t1 = getToken("u1@test.com", "password123");
        String t2 = getToken("u2@test.com", "password123");
        restTemplate.postForEntity("/api/projects",
                new HttpEntity<>(Map.of("name","P1"), authHeaders(t1)), ProjectResponse.class);
        restTemplate.postForEntity("/api/projects",
                new HttpEntity<>(Map.of("name","P2"), authHeaders(t2)), ProjectResponse.class);
        ResponseEntity<List<ProjectResponse>> r = restTemplate.exchange("/api/projects",
                HttpMethod.GET, new HttpEntity<>(authHeaders(t1)), new ParameterizedTypeReference<>(){});
        assertThat(r.getBody()).hasSize(1);
        assertThat(r.getBody().get(0).getName()).isEqualTo("P1");
    }

    @Test @Order(3)
    @DisplayName("GET /api/projects/{id} - 403 for non-owner")
    void getProjectForbidden() {
        String t1 = getToken("own@test.com", "password123");
        String t2 = getToken("other@test.com", "password123");
        Long id = restTemplate.postForEntity("/api/projects",
                new HttpEntity<>(Map.of("name","Private"), authHeaders(t1)), ProjectResponse.class)
                .getBody().getId();
        ResponseEntity<Map> r = restTemplate.exchange("/api/projects/" + id,
                HttpMethod.GET, new HttpEntity<>(authHeaders(t2)), Map.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test @Order(4)
    @DisplayName("PUT /api/projects/{id} - update project")
    void updateProject() {
        String token = getToken("upd@test.com", "password123");
        Long id = restTemplate.postForEntity("/api/projects",
                new HttpEntity<>(Map.of("name","Old"), authHeaders(token)), ProjectResponse.class)
                .getBody().getId();
        ResponseEntity<ProjectResponse> r = restTemplate.exchange("/api/projects/" + id,
                HttpMethod.PUT, new HttpEntity<>(Map.of("name","New","description","Updated"), authHeaders(token)),
                ProjectResponse.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody().getName()).isEqualTo("New");
    }

    @Test @Order(5)
    @DisplayName("DELETE /api/projects/{id} - delete project")
    void deleteProject() {
        String token = getToken("del@test.com", "password123");
        Long id = restTemplate.postForEntity("/api/projects",
                new HttpEntity<>(Map.of("name","ToDelete"), authHeaders(token)), ProjectResponse.class)
                .getBody().getId();
        ResponseEntity<Void> r = restTemplate.exchange("/api/projects/" + id,
                HttpMethod.DELETE, new HttpEntity<>(authHeaders(token)), Void.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test @Order(6)
    @DisplayName("POST /api/projects - 401 without token")
    void createProjectUnauthorized() {
        ResponseEntity<Map> r = restTemplate.postForEntity("/api/projects",
                Map.of("name","No Auth"), Map.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
