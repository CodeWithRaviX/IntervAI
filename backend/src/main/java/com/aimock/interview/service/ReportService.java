package com.aimock.interview.service;

import com.aimock.interview.dto.report.InterviewReportResponse;

import java.util.UUID;

public interface ReportService {
    InterviewReportResponse getReportByInterviewId(UUID userId, UUID interviewId);
}
