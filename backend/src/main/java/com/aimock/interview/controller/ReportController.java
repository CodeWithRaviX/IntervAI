package com.aimock.interview.controller;

import com.aimock.interview.dto.common.ApiResponse;
import com.aimock.interview.dto.report.InterviewReportResponse;
import com.aimock.interview.security.SecurityUtils;
import com.aimock.interview.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@Tag(name = "Interview Reports", description = "Detailed post-interview AI analysis and scoring reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{id}/report")
    @Operation(summary = "Get final interview report")
    public ResponseEntity<ApiResponse<InterviewReportResponse>> getReport(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        InterviewReportResponse response = reportService.getReportByInterviewId(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
