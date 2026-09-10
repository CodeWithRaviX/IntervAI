package com.aimock.interview.controller;

import com.aimock.interview.dto.answer.AnswerEvaluationResponse;
import com.aimock.interview.dto.answer.SubmitAnswerRequest;
import com.aimock.interview.dto.common.ApiResponse;
import com.aimock.interview.dto.common.PagedResponse;
import com.aimock.interview.dto.interview.CreateInterviewRequest;
import com.aimock.interview.dto.interview.InterviewDetailResponse;
import com.aimock.interview.dto.interview.InterviewResponse;
import com.aimock.interview.dto.interview.InterviewSummaryResponse;
import com.aimock.interview.security.SecurityUtils;
import com.aimock.interview.service.AnswerService;
import com.aimock.interview.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@Tag(name = "Interviews", description = "Core Mock Interview lifecycle endpoints")
public class InterviewController {

    private final InterviewService interviewService;
    private final AnswerService answerService;

    @PostMapping
    @Operation(summary = "Create a new interview session", description = "Creates a session with selected role, level, difficulty and total questions")
    public ResponseEntity<ApiResponse<InterviewResponse>> createInterview(@Valid @RequestBody CreateInterviewRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        InterviewResponse response = interviewService.createInterview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Interview session created"));
    }

    @GetMapping
    @Operation(summary = "Get user interview history", description = "Returns paginated list of interviews for the authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<InterviewSummaryResponse>>> getUserInterviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID userId = SecurityUtils.getCurrentUserId();
        PagedResponse<InterviewSummaryResponse> response = interviewService.getUserInterviews(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get interview session details", description = "Returns full details including all questions, answers, and evaluations")
    public ResponseEntity<ApiResponse<InterviewDetailResponse>> getInterviewDetails(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        InterviewDetailResponse response = interviewService.getInterviewDetails(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start interview session", description = "Triggers Gemini AI to generate the first question and transitions session to IN_PROGRESS")
    public ResponseEntity<ApiResponse<InterviewResponse>> startInterview(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        InterviewResponse response = interviewService.startInterview(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response, "Interview started"));
    }

    @PostMapping("/{id}/answers")
    @Operation(summary = "Submit answer to active question", description = "Evaluates candidate answer across 4 dimensions and generates adaptive follow-up question")
    public ResponseEntity<ApiResponse<AnswerEvaluationResponse>> submitAnswer(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitAnswerRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        AnswerEvaluationResponse response = answerService.submitAnswer(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Answer evaluated successfully"));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete interview session", description = "Finalizes interview and triggers Gemini AI to generate comprehensive post-interview report")
    public ResponseEntity<ApiResponse<InterviewDetailResponse>> completeInterview(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        InterviewDetailResponse response = interviewService.completeInterview(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response, "Interview completed successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel interview session", description = "Cancels active interview session")
    public ResponseEntity<ApiResponse<Void>> cancelInterview(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        interviewService.cancelInterview(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Interview cancelled"));
    }
}
