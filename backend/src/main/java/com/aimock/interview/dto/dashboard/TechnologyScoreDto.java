package com.aimock.interview.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnologyScoreDto {
    private String technology;
    private Double averageScore;
    private long interviewCount;
}
