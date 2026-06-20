package com.aravind.projectmanagementapi.service;

import com.aravind.projectmanagementapi.dto.auth.LoginRequest;
import com.aravind.projectmanagementapi.dto.auth.RegisterRequest;
import com.aravind.projectmanagementapi.dto.auth.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    String login(LoginRequest request);
}