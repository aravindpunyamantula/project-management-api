package com.aravind.projectmanagementapi.service.imp;

import com.aravind.projectmanagementapi.dto.auth.LoginRequest;
import com.aravind.projectmanagementapi.dto.auth.RegisterRequest;
import com.aravind.projectmanagementapi.dto.auth.RegisterResponse;
import com.aravind.projectmanagementapi.entity.User;
import com.aravind.projectmanagementapi.exception.InvalidCredentialsException;
import com.aravind.projectmanagementapi.exception.ResourceAlreadyExistsException;
import com.aravind.projectmanagementapi.respository.UserRepository;
import com.aravind.projectmanagementapi.security.JwtService;
import com.aravind.projectmanagementapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "Email already exists"
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .build();

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }

    @Override
    public String login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new InvalidCredentialsException(
                                "Invalid credentials"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException(
                    "Invalid credentials"
            );
        }

        return jwtService.generateToken(user.getEmail());
    }
}