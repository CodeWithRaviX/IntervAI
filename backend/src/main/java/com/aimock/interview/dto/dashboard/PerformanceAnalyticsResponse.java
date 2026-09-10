package com.aimock.interview.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceAnalyticsResponse {
    private List<TechnologyScoreDto> rolePerformance;
    private Map<String, Double> categoryScoreAverages; // technical, communication, relevance
    private List<String> strongSkills;
    private List<String> weakSkills;
    private List<Map<String, Object>> scoreTimeline;
}
