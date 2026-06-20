package com.aravind.projectmanagementapi;

import com.aravind.projectmanagementapi.dto.auth.AuthResponse;
import com.aravind.projectmanagementapi.dto.auth.RegisterResponse;
import com.aravind.projectmanagementapi.respository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/register - should register a new user successfully")
    void registerUser_success() {
        Map<String, String> request = Map.of(
                "email", "test@example.com",
                "password", "password123"
        );

        ResponseEntity<RegisterResponse> response = restTemplate.postForEntity(
                "/api/auth/register", request, RegisterResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/register - should return 409 for duplicate email")
    void registerUser_duplicateEmail() {
        Map<String, String> request = Map.of(
                "email", "duplicate@example.com",
                "password", "password123"
        );

        restTemplate.postForEntity("/api/auth/register", request, RegisterResponse.class);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/register", request, Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/register - should return 400 for missing email")
    void registerUser_missingEmail() {
        Map<String, String> request = Map.of(
                "password", "password123"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/register", request, Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/login - should login and return JWT token")
    void loginUser_success() {
        // Register first
        Map<String, String> registerRequest = Map.of(
                "email", "login@example.com",
                "password", "password123"
        );
        restTemplate.postForEntity("/api/auth/register", registerRequest, RegisterResponse.class);

        // Login
        Map<String, String> loginRequest = Map.of(
                "email", "login@example.com",
                "password", "password123"
        );

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
        assertThat(response.getBody().getTokenType()).isEqualTo("bearer");
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/auth/login - should return 401 for invalid credentials")
    void loginUser_invalidCredentials() {
        Map<String, String> request = Map.of(
                "email", "nonexistent@example.com",
                "password", "wrongpassword"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login", request, Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
