package com.aimock.interview.controller;

import com.aimock.interview.dto.common.ApiResponse;
import com.aimock.interview.dto.interview.InterviewResponse;
import com.aimock.interview.dto.resume.ResumeInterviewRequest;
import com.aimock.interview.dto.resume.ResumeUploadResponse;
import com.aimock.interview.security.SecurityUtils;
import com.aimock.interview.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Tag(name = "Resume-Based Interviews", description = "PDF Resume parsing and tailored question generation")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and parse PDF resume", description = "Extracts resume text, identifies skills & projects, and suggests matching job roles")
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> uploadResume(@RequestParam("file") MultipartFile file) {
        ResumeUploadResponse response = resumeService.parseAndAnalyzeResume(file);
        return ResponseEntity.ok(ApiResponse.success(response, "Resume analyzed successfully"));
    }

    @PostMapping("/interview")
    @Operation(summary = "Create interview session tailored to uploaded resume")
    public ResponseEntity<ApiResponse<InterviewResponse>> createResumeInterview(@Valid @RequestBody ResumeInterviewRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        InterviewResponse response = resumeService.createResumeBasedInterview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Resume-tailored interview created"));
    }
}
