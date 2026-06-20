package com.aravind.projectmanagementapi.controller;

import com.aravind.projectmanagementapi.dto.user.UserResponse;
import com.aravind.projectmanagementapi.entity.User;
import com.aravind.projectmanagementapi.exception.ResourceNotFoundException;
import com.aravind.projectmanagementapi.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));

        return new UserResponse(user.getId(), user.getEmail());
    }
}
