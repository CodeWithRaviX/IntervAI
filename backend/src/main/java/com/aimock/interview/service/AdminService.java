package com.aimock.interview.service;

import com.aimock.interview.dto.admin.AdminStatisticsResponse;
import com.aimock.interview.dto.admin.AdminUserResponse;
import com.aimock.interview.dto.common.PagedResponse;

import java.util.UUID;

public interface AdminService {
    PagedResponse<AdminUserResponse> getAllUsers(int page, int size);
    AdminUserResponse updateUserStatus(UUID userId, boolean enabled);
    AdminStatisticsResponse getSystemStatistics();
}
