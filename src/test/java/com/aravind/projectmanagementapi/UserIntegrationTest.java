package com.aravind.projectmanagementapi;

import com.aravind.projectmanagementapi.dto.auth.AuthResponse;
import com.aravind.projectmanagementapi.dto.auth.RegisterResponse;
import com.aravind.projectmanagementapi.dto.user.UserResponse;
import com.aravind.projectmanagementapi.respository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String getToken(String email, String password) {
        // Register
        Map<String, String> registerRequest = Map.of(
                "email", email,
                "password", password
        );
        restTemplate.postForEntity("/api/auth/register", registerRequest, RegisterResponse.class);

        // Login
        Map<String, String> loginRequest = Map.of(
                "email", email,
                "password", password
        );
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, AuthResponse.class
        );
        return loginResponse.getBody().getAccessToken();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/users/me - should return authenticated user profile")
    void getCurrentUser_success() {
        String token = getToken("profile@example.com", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/users/me", HttpMethod.GET, entity, UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("profile@example.com");
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/users/me - should return 401 without token")
    void getCurrentUser_unauthorized() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/users/me", Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
