package com.aimock.interview.service.impl;

import com.aimock.interview.ai.GeminiService;
import com.aimock.interview.dto.ai.GeminiResumeAnalysisResponse;
import com.aimock.interview.dto.interview.CreateInterviewRequest;
import com.aimock.interview.dto.interview.InterviewResponse;
import com.aimock.interview.dto.resume.ResumeInterviewRequest;
import com.aimock.interview.dto.resume.ResumeUploadResponse;
import com.aimock.interview.entity.Interview;
import com.aimock.interview.entity.InterviewStatus;
import com.aimock.interview.entity.User;
import com.aimock.interview.exception.BadRequestException;
import com.aimock.interview.exception.ResourceNotFoundException;
import com.aimock.interview.repository.InterviewRepository;
import com.aimock.interview.repository.UserRepository;
import com.aimock.interview.service.AuditLogService;
import com.aimock.interview.service.InterviewService;
import com.aimock.interview.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final GeminiService geminiService;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final AuditLogService auditLogService;

    @Override
    public ResumeUploadResponse parseAndAnalyzeResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded resume file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("Only PDF resumes are supported (.pdf)");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new BadRequestException("Resume file size exceeds maximum limit of 5MB");
        }

        String extractedText;
        try (InputStream is = file.getInputStream();
             PDDocument document = Loader.loadPDF(is.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            extractedText = stripper.getText(document);
        } catch (Exception e) {
            log.error("Failed to parse PDF resume: {}", e.getMessage(), e);
            throw new BadRequestException("Unable to read text from the uploaded PDF resume. Ensure the PDF contains selectable text.");
        }

        if (extractedText == null || extractedText.trim().length() < 50) {
            throw new BadRequestException("The uploaded PDF resume contains too little text for meaningful interview question generation.");
        }

        GeminiResumeAnalysisResponse analysis = geminiService.analyzeResume(extractedText);

        return ResumeUploadResponse.builder()
                .extractedText(extractedText)
                .detectedSkills(analysis.getDetectedSkills())
                .detectedProjects(analysis.getDetectedProjects())
                .suggestedRole(analysis.getSuggestedRole())
                .build();
    }

    @Override
    @Transactional
    public InterviewResponse createResumeBasedInterview(UUID userId, ResumeInterviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Interview interview = Interview.builder()
                .user(user)
                .jobRole(request.getJobRole())
                .experienceLevel(request.getExperienceLevel())
                .difficulty(request.getDifficulty())
                .interviewType(request.getInterviewType())
                .totalQuestions(request.getTotalQuestions() != null ? request.getTotalQuestions() : 5)
                .completedQuestions(0)
                .status(InterviewStatus.CREATED)
                .resumeExtractedText(request.getResumeText())
                .build();

        Interview saved = interviewRepository.save(interview);
        auditLogService.logAction(userId, "CREATE_RESUME_INTERVIEW", "INTERVIEW", saved.getId().toString(), "Resume-tailored interview session created");

        return InterviewResponse.builder()
                .id(saved.getId())
                .jobRole(saved.getJobRole())
                .experienceLevel(saved.getExperienceLevel())
                .difficulty(saved.getDifficulty())
                .interviewType(saved.getInterviewType())
                .status(saved.getStatus())
                .totalQuestions(saved.getTotalQuestions())
                .completedQuestions(saved.getCompletedQuestions())
                .overallScore(saved.getOverallScore())
                .currentQuestion(null)
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
