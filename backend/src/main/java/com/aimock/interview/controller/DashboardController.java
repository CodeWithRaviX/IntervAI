package com.aimock.interview.controller;

import com.aimock.interview.dto.common.ApiResponse;
import com.aimock.interview.dto.dashboard.DashboardSummaryResponse;
import com.aimock.interview.dto.dashboard.PerformanceAnalyticsResponse;
import com.aimock.interview.security.SecurityUtils;
import com.aimock.interview.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "User dashboard analytics, performance metrics, and skill breakdowns")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary metrics", description = "Returns total interviews, avg score, best score, recent sessions, and tech breakdown")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        UUID userId = SecurityUtils.getCurrentUserId();
        DashboardSummaryResponse response = dashboardService.getDashboardSummary(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/performance")
    @Operation(summary = "Get detailed performance analytics", description = "Returns multi-dimensional scores, category breakdowns, and performance trends over time")
    public ResponseEntity<ApiResponse<PerformanceAnalyticsResponse>> getPerformance() {
        UUID userId = SecurityUtils.getCurrentUserId();
        PerformanceAnalyticsResponse response = dashboardService.getPerformanceAnalytics(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
