package com.aimock.interview.service;

import com.aimock.interview.dto.dashboard.DashboardSummaryResponse;
import com.aimock.interview.dto.dashboard.PerformanceAnalyticsResponse;

import java.util.UUID;

public interface DashboardService {
    DashboardSummaryResponse getDashboardSummary(UUID userId);
    PerformanceAnalyticsResponse getPerformanceAnalytics(UUID userId);
}
