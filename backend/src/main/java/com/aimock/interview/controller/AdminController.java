package com.aimock.interview.controller;

import com.aimock.interview.dto.admin.AdminStatisticsResponse;
import com.aimock.interview.dto.admin.AdminUserResponse;
import com.aimock.interview.dto.admin.AdminUserStatusUpdateRequest;
import com.aimock.interview.dto.common.ApiResponse;
import com.aimock.interview.dto.common.PagedResponse;
import com.aimock.interview.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "System administration, user management, and global statistics")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "Get all users (Admin only)", description = "Returns paginated list of registered users and their interview activity")
    public ResponseEntity<ApiResponse<PagedResponse<AdminUserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<AdminUserResponse> response = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Update user active status (Admin only)", description = "Enables or disables a user account")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUserStatusUpdateRequest request) {
        AdminUserResponse response = adminService.updateUserStatus(id, request.getEnabled());
        return ResponseEntity.ok(ApiResponse.success(response, "User status updated"));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get global system statistics (Admin only)", description = "Returns system wide user and interview statistics")
    public ResponseEntity<ApiResponse<AdminStatisticsResponse>> getSystemStatistics() {
        AdminStatisticsResponse response = adminService.getSystemStatistics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
