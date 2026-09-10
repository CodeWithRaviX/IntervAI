package com.aimock.interview.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsResponse {
    private long totalUsers;
    private long activeUsers;
    private long totalInterviews;
    private long completedInterviews;
    private long inProgressInterviews;
    private Map<String, Long> interviewsByRole;
}
