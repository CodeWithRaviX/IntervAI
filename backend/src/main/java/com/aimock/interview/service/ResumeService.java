package com.aimock.interview.service;

import com.aimock.interview.dto.interview.InterviewResponse;
import com.aimock.interview.dto.resume.ResumeInterviewRequest;
import com.aimock.interview.dto.resume.ResumeUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ResumeService {
    ResumeUploadResponse parseAndAnalyzeResume(MultipartFile file);
    InterviewResponse createResumeBasedInterview(UUID userId, ResumeInterviewRequest request);
}
