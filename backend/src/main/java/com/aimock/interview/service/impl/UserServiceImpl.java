package com.aimock.interview.service.impl;

import com.aimock.interview.dto.auth.UpdateProfileRequest;
import com.aimock.interview.dto.auth.UserResponse;
import com.aimock.interview.entity.User;
import com.aimock.interview.exception.ResourceNotFoundException;
import com.aimock.interview.exception.UnauthorizedException;
import com.aimock.interview.repository.UserRepository;
import com.aimock.interview.security.SecurityUtils;
import com.aimock.interview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(UUID userId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null || (!currentUserId.equals(userId) && !SecurityUtils.hasRole("ROLE_ADMIN"))) {
            throw new UnauthorizedException("You are not authorized to view this user profile");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null || (!currentUserId.equals(userId) && !SecurityUtils.hasRole("ROLE_ADMIN"))) {
            throw new UnauthorizedException("You are not authorized to update this profile");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setFullName(request.getFullName().trim());
        user.setHeadline(request.getHeadline());
        user.setTargetRole(request.getTargetRole());
        user.setYearsOfExperience(request.getYearsOfExperience());

        User updated = userRepository.save(user);
        return mapToUserResponse(updated);
    }

    private UserResponse mapToUserResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .headline(user.getHeadline())
                .targetRole(user.getTargetRole())
                .yearsOfExperience(user.getYearsOfExperience())
                .enabled(user.isEnabled())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
