package com.aimock.interview.service;

import com.aimock.interview.dto.common.PagedResponse;
import com.aimock.interview.dto.interview.CreateInterviewRequest;
import com.aimock.interview.dto.interview.InterviewDetailResponse;
import com.aimock.interview.dto.interview.InterviewResponse;
import com.aimock.interview.dto.interview.InterviewSummaryResponse;

import java.util.UUID;

public interface InterviewService {
    InterviewResponse createInterview(UUID userId, CreateInterviewRequest request);
    InterviewResponse startInterview(UUID userId, UUID interviewId);
    InterviewDetailResponse getInterviewDetails(UUID userId, UUID interviewId);
    PagedResponse<InterviewSummaryResponse> getUserInterviews(UUID userId, int page, int size);
    InterviewDetailResponse completeInterview(UUID userId, UUID interviewId);
    void cancelInterview(UUID userId, UUID interviewId);
}
