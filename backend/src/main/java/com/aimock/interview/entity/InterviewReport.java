package com.aimock.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_reports", indexes = {
    @Index(name = "idx_reports_interview_id", columnList = "interview_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false, unique = true)
    private Interview interview;

    @Column(name = "overall_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "technical_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal technicalScore;

    @Column(name = "communication_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal communicationScore;

    @Column(name = "relevance_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal relevanceScore;

    @Column(name = "average_answer_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal averageAnswerScore;

    @Column(name = "strengths_json", columnDefinition = "TEXT")
    private String strengthsJson;

    @Column(name = "weaknesses_json", columnDefinition = "TEXT")
    private String weaknessesJson;

    @Column(name = "recommended_topics_json", columnDefinition = "TEXT")
    private String recommendedTopicsJson;

    @Column(name = "improvement_suggestions_json", columnDefinition = "TEXT")
    private String improvementSuggestionsJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
