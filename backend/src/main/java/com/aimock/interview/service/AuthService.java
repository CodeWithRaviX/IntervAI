package com.aimock.interview.service;

import com.aimock.interview.dto.auth.LoginRequest;
import com.aimock.interview.dto.auth.LoginResponse;
import com.aimock.interview.dto.auth.RegisterRequest;
import com.aimock.interview.dto.auth.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    UserResponse getCurrentUser();
}
