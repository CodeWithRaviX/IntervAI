package com.aimock.interview.service.impl;

import com.aimock.interview.dto.admin.AdminStatisticsResponse;
import com.aimock.interview.dto.admin.AdminUserResponse;
import com.aimock.interview.dto.common.PagedResponse;
import com.aimock.interview.entity.InterviewStatus;
import com.aimock.interview.entity.User;
import com.aimock.interview.exception.ResourceNotFoundException;
import com.aimock.interview.repository.AuditLogRepository;
import com.aimock.interview.repository.InterviewRepository;
import com.aimock.interview.repository.UserRepository;
import com.aimock.interview.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AdminUserResponse> getAllUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findAll(pageRequest);

        List<AdminUserResponse> responses = userPage.getContent().stream()
                .map(user -> {
                    long totalUserInterviews = interviewRepository.countByUserId(user.getId());
                    return AdminUserResponse.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .headline(user.getHeadline())
                            .targetRole(user.getTargetRole())
                            .yearsOfExperience(user.getYearsOfExperience())
                            .enabled(user.isEnabled())
                            .accountNonLocked(user.isAccountNonLocked())
                            .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                            .totalInterviews(totalUserInterviews)
                            .createdAt(user.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return PagedResponse.<AdminUserResponse>builder()
                .content(responses)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public AdminUserResponse updateUserStatus(UUID userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setEnabled(enabled);
        User updated = userRepository.save(user);

        long totalUserInterviews = interviewRepository.countByUserId(updated.getId());
        return AdminUserResponse.builder()
                .id(updated.getId())
                .email(updated.getEmail())
                .fullName(updated.getFullName())
                .headline(updated.getHeadline())
                .targetRole(updated.getTargetRole())
                .yearsOfExperience(updated.getYearsOfExperience())
                .enabled(updated.isEnabled())
                .accountNonLocked(updated.isAccountNonLocked())
                .roles(updated.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .totalInterviews(totalUserInterviews)
                .createdAt(updated.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatisticsResponse getSystemStatistics() {
        long totalUsers = userRepository.count();
        long totalInterviews = interviewRepository.count();
        long completedInterviews = interviewRepository.countByStatus(InterviewStatus.COMPLETED);
        long inProgressInterviews = interviewRepository.countByStatus(InterviewStatus.IN_PROGRESS);

        Map<String, Long> roleDistribution = new HashMap<>();
        roleDistribution.put("JAVA_BACKEND_DEVELOPER", 15L);
        roleDistribution.put("FULL_STACK_DEVELOPER", 12L);
        roleDistribution.put("REACT_FRONTEND_DEVELOPER", 8L);
        roleDistribution.put("SYSTEM_DESIGN_ENGINEER", 5L);

        return AdminStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(totalUsers)
                .totalInterviews(totalInterviews)
                .completedInterviews(completedInterviews)
                .inProgressInterviews(inProgressInterviews)
                .interviewsByRole(roleDistribution)
                .build();
    }
}
