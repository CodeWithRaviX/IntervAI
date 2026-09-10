package com.aimock.interview.service;

import com.aimock.interview.dto.auth.UpdateProfileRequest;
import com.aimock.interview.dto.auth.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse getUserProfile(UUID userId);
    UserResponse updateProfile(UUID userId, UpdateProfileRequest request);
}
